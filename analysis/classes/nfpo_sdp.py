import numpy as np
from pyomo.environ import *
from pyomo.opt import SolverFactory


class NFPO_SDP:
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
        # phases =
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

    def __cal_mean(self):

        mean = np.mean(self.phases, axis=0)
        return mean

    def __set_cond(self):
        mean = self.__cal_mean()
        self.__cond2 = [
            [
                (
                    0
                    if i < j
                    else -2 * self.tags_space * (i - j) / self.wave_length
                    - 1 / (2 * np.pi) * (mean[i] + mean[j])
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
                    else 2 * self.tags_space * (i - j) / self.wave_length
                    - 1 / (2 * np.pi) * (mean[i] - mean[j])
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
                    else -2 * self.tags_space * (i - j) / self.wave_length
                    - 1 / (2 * np.pi) * (mean[i] - mean[j])
                )
                for j in range(self.tag_population)
            ]
            for i in range(self.tag_population)
        ]

    def __objective_rule(self, model):
        std = self.__cal_std()
        mean = self.__cal_mean()
        return sum(
            (
                (mean[m] + 2 * model.n[m] * np.pi) ** 2
                + 8
                * np.pi
                / self.wave_length
                * (mean[m] + 2 * model.n[m] * np.pi)
                * model.h[m]
                + (4 * np.pi / self.wave_length) ** 2 * model.g[m]
            )
            / (std[m] ** 2)
            for m in model.M
        )

    def __n_initialize(self, model, i):
        return -1

    def __g_initialize(self, model, i):
        return (
            (self.initial_value["x"] + i * self.tags_space) ** 2
            + self.initial_value["y"] ** 2
        ) ** 2

    def __h_initialize(self, model, i):
        return sqrt(
            (self.initial_value["x"] + i * self.tags_space) ** 2
            + self.initial_value["y"] ** 2
        )

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

    def __constraint_rule6(self, model, i):
        return model.g[i] == ((model.x + i * self.tags_space) ** 2 + model.y**2) ** 2

    def __constraint_rule7(self, model, i):
        return model.g[i] == model.h[i] ** 2

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
        self.__model.g = Var(
            self.__model.M,
            within=Reals,
            initialize=self.__g_initialize,
        )
        self.__model.h = Var(
            self.__model.M,
            within=Reals,
            initialize=self.__h_initialize,
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
        self.__model.constraint6 = Constraint(
            range(self.tag_population), rule=self.__constraint_rule6
        )
        self.__model.constraint7 = Constraint(
            range(self.tag_population), rule=self.__constraint_rule7
        )

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
                # "g": self.__model.g[:](),
                # "h": self.__model.h[:](),
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
