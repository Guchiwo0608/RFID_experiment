import numpy as np
from pyomo.environ import *
from pyomo.opt import SolverFactory


class NFPO:
    def __init__(
        self,
        sample_count,
        tags_space,
        phases,
        tag_population,
        wave_length,
        tag_location,
        initial_value,
        x_err_range,
        y_err_range,
    ) -> None:

        self.sample_count = sample_count
        self.tags_space = tags_space
        # phases.shape = (sample_count, tags_population)
        self.phases = phases
        self.tag_population = tag_population
        self.wave_length = wave_length
        self.tag_location = tag_location
        self.initial_value = initial_value
        self.x_err_range = x_err_range
        self.y_err_range = y_err_range

        self.__cond2 = []
        self.__cond3 = []
        self.__cond4 = []

        self.__model = ConcreteModel(name="Nonconvex MINLP sample")

        self.__set_cond()

    def __cal_std(self):

        std = np.std(self.phases, axis=0)
        return std

    def __set_cond(self):
        self.__cond2 = [
            [
                (
                    0
                    if i < j
                    else (1 / self.sample_count)
                    * sum(
                        -2 * self.tags_space * (i - j) / self.wave_length
                        - 1 / (2 * np.pi) * (self.phases[k][i] + self.phases[k][j])
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.tag_population)
            ]
            for i in range(self.tag_population)
        ]

        self.__cond3 = [
            [
                (
                    0
                    if i <= j
                    else (1 / self.sample_count)
                    * sum(
                        2 * self.tags_space * (i - j) / self.wave_length
                        - 1 / (2 * np.pi) * (self.phases[k][i] - self.phases[k][j])
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.tag_population)
            ]
            for i in range(self.tag_population)
        ]

        self.__cond4 = [
            [
                (
                    0
                    if i <= j
                    else (1 / self.sample_count)
                    * sum(
                        -2 * self.tags_space * (i - j) / self.wave_length
                        - 1 / (2 * np.pi) * (self.phases[k][i] - self.phases[k][j])
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.tag_population)
            ]
            for i in range(self.tag_population)
        ]

    def __objective_rule(self, model):
        return (1 / self.sample_count) * sum(
            sum(
                (
                    (model.x + m * self.tags_space) ** 2
                    + model.y**2
                    - (
                        self.wave_length
                        / 4
                        / np.pi
                        * (self.phases[i][m] + 2 * np.pi * model.n[m])
                    )
                    ** 2
                )
                ** 2
                for m in model.M
            )
            for i in model.I
        )

    def __n_initialize(self, model, i):
        return -1

    def __constraint_rule1(self, model: ConcreteModel, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] + model.n[j] <= self.__cond2[i][j] + 0.00000001

    def __constraint_rule2(self, model, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] - model.n[j] <= self.__cond3[i][j] + 0.00000001

    def __constraint_rule3(self, model, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] - model.n[j] >= self.__cond4[i][j] - 0.00000001

    def __constraint_rule4(self, model):
        return abs(model.x - self.tag_location["x"]) <= self.x_err_range

    def __constraint_rule5(self, model):
        return abs(model.y - self.tag_location["y"]) <= self.y_err_range

    def __set_parameters(self):
        self.__model.x = Var(
            within=Reals,
            bounds=(-2, 2),
            initialize=self.initial_value["x"],
        )
        self.__model.y = Var(
            within=Reals,
            bounds=(0, 2),
            initialize=self.initial_value["y"],
        )
        self.__model.M = Set(initialize=range(self.tag_population))
        self.__model.n = Var(
            self.__model.M,
            within=NegativeIntegers,
            bounds=(-30, -1),
            initialize=self.__n_initialize,
        )
        self.__model.I = Set(initialize=range(self.sample_count))

    def solve(self, tee=False):
        self.__set_parameters()
        self.__model.obj = Objective(rule=self.__objective_rule, sense=minimize)
        self.__model.constraint1 = Constraint(
            range(self.tag_population),
            range(self.tag_population),
            rule=self.__constraint_rule1,
        )
        self.__model.constraint2 = Constraint(
            range(self.tag_population),
            range(self.tag_population),
            rule=self.__constraint_rule2,
        )
        self.__model.constraint3 = Constraint(
            range(self.tag_population),
            range(self.tag_population),
            rule=self.__constraint_rule3,
        )
        self.__model.constraint4 = Constraint(rule=self.__constraint_rule4)
        self.__model.constraint5 = Constraint(rule=self.__constraint_rule5)
        opt = SolverFactory("scip")
        res = opt.solve(self.__model, tee=tee)
        result = {
            "solving_time": res.json_repn()["Solver"][0]["Time"],
            "primal_bound": res.json_repn()["Solver"][0]["Primal bound"],
            "dual_bound": res.json_repn()["Solver"][0]["Dual bound"],
            "values": {
                "x": self.__model.x(),
                "y": self.__model.y(),
                "N": self.__model.n[:](),
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
        tag_location: dict = None,
        x_err_range: float = None,
        y_err_range: float = None,
        initial_value: dict = None,
    ):
        if tags_space != None:
            self.antennas_space = tags_space
        if tag_population != None:
            self.antenna_population = tag_population
        if x_err_range != None:
            self.x_err_range = x_err_range
        if y_err_range != None:
            self.y_err_range = y_err_range
        if tag_location != None:
            self.tag_location = tag_location
        if initial_value != None:
            self.initial_value = initial_value

        for var in self.__model.component_objects(Var):
            self.__model.del_component(var)
        for set in self.__model.component_objects(Set):
            self.__model.del_component(set)
