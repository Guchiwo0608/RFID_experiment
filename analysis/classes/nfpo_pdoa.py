import numpy as np
from pyomo.environ import *
from pyomo.opt import SolverFactory


class NFPO_PDOA:
    def __init__(
        self,
        sample_count,
        tags_space,
        phases1,
        phases2,
        tag_population,
        frequency1,
        frequency2,
        tag_location,
        initial_value,
        x_err_range,
        y_err_range,
    ) -> None:

        self.sample_count = sample_count
        self.tags_space = tags_space
        self.phases1 = phases1
        self.phases2 = phases2
        self.tag_population = tag_population
        self.frequency1 = frequency1
        self.frequency2 = frequency2
        self.tag_location = tag_location
        self.initial_value = initial_value
        self.x_err_range = x_err_range
        self.y_err_range = y_err_range
        self.light_speed = 2.998e8

        self.wave_length1 = self.light_speed / self.frequency1
        self.wave_length2 = self.light_speed / self.frequency2

        self.__model = ConcreteModel(name="nfpo pdoa sample")

    def __set_parameters(self):
        self.__model.x = Var(
            within=Reals, bounds=(-2, 2), initialize=self.initial_value["x"]
        )
        self.__model.y = Var(
            within=Reals, bounds=(0, 2), initialize=self.initial_value["y"]
        )
        self.__model.M = Set(initialize=range(self.tag_population))
        self.__model.I = Set(initialize=range(self.sample_count))

    def __objective_rule(self, model):
        delta_N = [
            [
                -1 if self.phases1[i][m] < self.phases2[i][m] else 0
                for m in range(self.tag_population)
            ]
            for i in range(self.sample_count)
        ]
        delta_phase = [
            [
                self.phases1[i][m] - self.phases2[i][m]
                for m in range(self.tag_population)
            ]
            for i in range(self.sample_count)
        ]
        return (1 / self.sample_count) * sum(
            sum(
                (
                    (model.x + m * self.tags_space) ** 2
                    + model.y**2
                    - (
                        -self.light_speed
                        / (self.frequency1 - self.frequency2)
                        / 4
                        / np.pi
                        * (2 * np.pi * (delta_N[i][m]) + (delta_phase[i][m]))
                    )
                    ** 2
                )
                ** 2
                for m in model.M
            )
            for i in model.I
        )

    def solve(self):
        self.__set_parameters()
        self.__model.obj = Objective(rule=self.__objective_rule, sense=minimize)
        opt = SolverFactory("scip")
        res = opt.solve(self.__model, tee=False)
        result = {
            "solving_time": res.json_repn()["Solver"][0]["Time"],
            "primal_bound": res.json_repn()["Solver"][0]["Primal bound"],
            "dual_bound": res.json_repn()["Solver"][0]["Dual bound"],
            "values": {
                "x": self.__model.x(),
                "y": self.__model.y(),
            },
        }
        return result

    def del_component(self):
        for obj in self.__model.component_objects(Objective):
            self.__model.del_component(obj)
        for con in self.__model.component_objects(Constraint):
            self.__model.del_component(con)

    def renew_parameter(
        self,
        tags_space: float = None,
        tag_population: int = None,
        x_err_range: float = None,
        y_err_range: float = None,
    ):
        if tags_space != None:
            self.tags_space = tags_space
        if tag_population != None:
            self.tag_population = tag_population
        if x_err_range != None:
            self.x_err_range = x_err_range
        if y_err_range != None:
            self.y_err_range = y_err_range

        for var in self.__model.component_objects(Var):
            self.__model.del_component(var)
        for set in self.__model.component_objects(Set):
            self.__model.del_component(set)
