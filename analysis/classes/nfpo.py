import signal
import numpy as np
from pyomo.environ import *
from pyomo.opt import SolverFactory
import pyomo.core.expr.numeric_expr as po_expr
import math


class NFPO:
    def __init__(
        self,
        sample_count,
        antennas_space,
        phases,
        antennas_population,
        wave_length,
        tag_location,
        initial_value,
        x_err_range,
        y_err_range,
        k,
        is_noise_included,
        range_center: np.ndarray = None,
        initial_n: np.ndarray = None,
    ) -> None:

        self.sample_count = sample_count
        self.antennas_space = antennas_space
        # phases.shape = (sample_count, antennas_population)
        self.phases = phases
        self.antennas_population = antennas_population
        self.wave_length = wave_length
        self.tag_location = tag_location
        self.initial_value = initial_value
        self.x_err_range = x_err_range
        self.y_err_range = y_err_range
        if range_center is None:
            self.range_center = initial_value
        else:
            self.range_center = range_center
        self.k = k
        self.is_noise_included = is_noise_included
        if initial_n is None:
            self.initial_n = (
                -4
                * np.pi
                / wave_length
                * np.sqrt(
                    (initial_value[0] - np.arange(antennas_population) * antennas_space)
                    ** 2
                    + initial_value[1] ** 2
                )
                + k
                * np.arctan(
                    (initial_value[0] - np.arange(antennas_population) * antennas_space)
                    / initial_value[1]
                )
            ) // (2 * np.pi)
        else:
            self.initial_n = initial_n

        self.__cond1 = []
        self.__cond2 = []
        self.__cond3 = []

        self.__model = ConcreteModel(name="Nonconvex MINLP sample")

    def __set_cond(self):
        self.__cond1 = [
            [
                (
                    0
                    if i < j
                    else (1 / self.sample_count)
                    * sum(
                        (
                            -2 * self.antennas_space * (i - j) / self.wave_length
                            - 1
                            / (2 * np.pi)
                            * (
                                self.phases[k][i]
                                + self.phases[k][j]
                                - (
                                    self.k
                                    * (
                                        np.arctan(
                                            value(
                                                self.__model.x - i * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                        + np.arctan(
                                            value(
                                                self.__model.x - j * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                    )
                                )
                                if self.is_noise_included
                                else 0
                                # - (
                                #     self.k * (self.__model.t[i] + self.__model.t[j])
                                #     if self.is_noise_included
                                #     else 0
                                # )
                            )
                        )
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.antennas_population)
            ]
            for i in range(self.antennas_population)
        ]

        self.__cond2 = [
            [
                (
                    0
                    if i <= j
                    else (1 / self.sample_count)
                    * sum(
                        (
                            2 * self.antennas_space * (i - j) / self.wave_length
                            - 1
                            / (2 * np.pi)
                            * (
                                self.phases[k][i]
                                - self.phases[k][j]
                                - (
                                    self.k
                                    * (
                                        np.arctan(
                                            value(
                                                self.__model.x - i * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                        - np.arctan(
                                            value(
                                                self.__model.x - j * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                    )
                                    if self.is_noise_included
                                    else 0
                                )
                                # - (
                                #     self.k * (self.__model.t[i] - self.__model.t[j])
                                #     if self.is_noise_included
                                #     else 0
                                # )
                            )
                        )
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.antennas_population)
            ]
            for i in range(self.antennas_population)
        ]

        self.__cond3 = [
            [
                (
                    0
                    if i <= j
                    else (1 / self.sample_count)
                    * sum(
                        (
                            -2 * self.antennas_space * (i - j) / self.wave_length
                            - 1
                            / (2 * np.pi)
                            * (
                                self.phases[k][i]
                                - self.phases[k][j]
                                - (
                                    self.k
                                    * (
                                        np.arctan(
                                            value(
                                                self.__model.x - i * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                        - np.arctan(
                                            value(
                                                self.__model.x - j * self.antennas_space
                                            )
                                            / value(self.__model.y)
                                        )
                                    )
                                    if self.is_noise_included
                                    else 0
                                )
                                # - (
                                #     self.k * (self.__model.t[i] - self.__model.t[j])
                                #     if self.is_noise_included
                                #     else 0
                                # )
                            )
                        )
                        for k in range(self.sample_count)
                    )
                )
                for j in range(self.antennas_population)
            ]
            for i in range(self.antennas_population)
        ]

    def __objective_rule(self, model):
        return (1 / self.sample_count) * sum(
            sum(
                (
                    (model.x - m * self.antennas_space) ** 2
                    + model.y**2
                    - (
                        -self.wave_length
                        / (4 * np.pi)
                        * (
                            self.phases[i][m]
                            + 2 * np.pi * model.n[m]
                            - (
                                self.k
                                * np.arctan(
                                    value(model.x - m * self.antennas_space)
                                    / value(model.y)
                                )
                                if self.is_noise_included
                                else 0
                            )
                        )
                    )
                    ** 2
                )
                ** 2
                for m in model.M
            )
            for i in model.I
        )

    def __n_initialize(self, model, i):
        return self.initial_n[i]

    def __constraint_rule1(self, model: ConcreteModel, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] + model.n[j] <= self.__cond1[i][j] + 1e-09

    def __constraint_rule2(self, model, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] - model.n[j] <= self.__cond2[i][j] + 1e-09

    def __constraint_rule3(self, model, i, j):
        if i <= j:
            return Constraint.Skip
        else:
            return model.n[i] - model.n[j] >= self.__cond3[i][j] - 1e-09

    def __constraint_rule4(self, model):
        return abs(model.x - self.range_center[0]) <= self.x_err_range

    def __constraint_rule5(self, model):
        return abs(model.y - self.range_center[1]) <= self.y_err_range

    def __constraint_rule7(self, model, m):
        return (
            abs(
                (1 / self.sample_count)
                * sum(
                    (model.x - m * self.antennas_space)
                    - (
                        (
                            -self.wave_length
                            / (4 * np.pi)
                            * (
                                self.phases[i][m]
                                + 2 * np.pi * model.n[m]
                                - (
                                    self.k
                                    * np.arctan(
                                        value(model.x - m * self.antennas_space)
                                        / value(model.y)
                                    )
                                    if self.is_noise_included
                                    else 0
                                )
                            )
                        )
                        * sin(
                            np.arctan(
                                value(model.x - m * self.antennas_space)
                                / value(model.y)
                            )
                        )
                    )
                    for i in model.I
                )
            )
            <= 1e-09
        )

    def __constraint_rule8(self, model, m):
        return (
            abs(
                (1 / self.sample_count)
                * sum(
                    (model.y)
                    - (
                        (
                            -self.wave_length
                            / (4 * np.pi)
                            * (
                                self.phases[i][m]
                                + 2 * np.pi * model.n[m]
                                - (
                                    self.k
                                    * np.arctan(
                                        value(model.x - m * self.antennas_space)
                                        / value(model.y)
                                    )
                                    if self.is_noise_included
                                    else 0
                                )
                            )
                        )
                        * cos(
                            np.arctan(
                                value(model.x - m * self.antennas_space)
                                / value(model.y)
                            )
                        )
                    )
                    for i in model.I
                )
            )
            <= 1e-09
        )

    def __set_parameters(self):
        self.__model.x = Var(
            within=Reals,
            bounds=(-3, 3),
            initialize=self.initial_value[0],
        )
        self.__model.y = Var(
            within=Reals,
            bounds=(0, 5),
            initialize=self.initial_value[1],
        )
        self.__model.M = Set(initialize=range(self.antennas_population))
        self.__model.n = Var(
            self.__model.M,
            within=NegativeIntegers,
            bounds=(-50, -1),
            initialize=self.__n_initialize,
        )
        self.__model.I = Set(initialize=range(self.sample_count))

    def solve(self, tee=False, time_limit: float = 180):
        self.__set_parameters()
        self.__set_cond()
        self.__model.obj = Objective(rule=self.__objective_rule, sense=minimize)
        self.__model.constraint1 = Constraint(
            range(self.antennas_population),
            range(self.antennas_population),
            rule=self.__constraint_rule1,
        )
        self.__model.constraint2 = Constraint(
            range(self.antennas_population),
            range(self.antennas_population),
            rule=self.__constraint_rule2,
        )
        self.__model.constraint3 = Constraint(
            range(self.antennas_population),
            range(self.antennas_population),
            rule=self.__constraint_rule3,
        )

        self.__model.constraint4 = Constraint(rule=self.__constraint_rule4)
        self.__model.constraint5 = Constraint(rule=self.__constraint_rule5)
        # if self.is_noise_included:
        #     self.__model.constraint7 = Constraint(
        #         range(self.antennas_population),
        #         rule=self.__constraint_rule7,
        #     )
        #     self.__model.constraint8 = Constraint(
        #         range(self.antennas_population),
        #         rule=self.__constraint_rule8,
        #     )
        opt = SolverFactory("scip")
        if tee:
            for v in self.__model.component_objects():
                v.pprint()
        res = opt.solve(self.__model, tee=tee, timelimit=time_limit)
        value_x = self.__model.x()
        value_y = self.__model.y()
        value_N = np.array(self.__model.n[:]())
        error_vector = np.array(
            [value_x - self.tag_location[0], value_y - self.tag_location[1]]
        )
        angles = np.arctan(
            value_x
            - np.arange(self.antennas_population) * self.antennas_space / value_y
        )

        # gap = self.cul_gap(value_x, value_y, value_N)
        # gap_diff = self.cul_gap_diff(value_x, value_y, value_N)
        obj_value = self.__model.obj()

        error = np.linalg.norm(error_vector, ord=2)
        result = {
            "solving_time": res.json_repn()["Solver"][0]["Time"],
            "values": {
                "x": value_x,
                "y": value_y,
                "N": value_N,
                "angle": angles,
                "error": error,
                "error_vector": error_vector,
                "obj": obj_value,
            },
        }
        return result

    def cul_gap(self, x, y, N):

        gap = (1 / self.sample_count) * np.sum(
            np.sum(
                (
                    (x - np.arange(self.antennas_population) * self.antennas_space) ** 2
                    + y**2
                    - (
                        -self.wave_length
                        / (4 * np.pi)
                        * (
                            self.phases.mean()
                            + 2 * np.pi * N.astype(int)
                            - (
                                self.k
                                * np.arctan(
                                    (
                                        x
                                        - np.arange(self.antennas_population)
                                        * self.antennas_space
                                    )
                                    / (y)
                                )
                            )
                        )
                    )
                    ** 2
                )
            )
        )

        return np.abs(gap)

    def cul_gap_diff(self, x, y, N):

        result_gap = self.cul_gap(x, y, N)

        correct_n = (
            -4
            * np.pi
            / self.wave_length
            * np.sqrt(
                (
                    self.tag_location[0]
                    - np.arange(self.antennas_population) * self.antennas_space
                )
                ** 2
                + self.tag_location[1] ** 2
            )
            + self.k
            * np.arctan(
                (
                    self.tag_location[0]
                    - np.arange(self.antennas_population) * self.antennas_space
                )
                / (self.tag_location[1])
            )
        ) // (2 * np.pi)

        correct_gap = self.cul_gap(
            self.tag_location[0], self.tag_location[1], correct_n
        )

        return result_gap - correct_gap

    def del_component(self):
        for obj in self.__model.component_objects(Objective):
            self.__model.del_component(obj)
        for con in self.__model.component_objects(Constraint):
            self.__model.del_component(con)

    def renew_parameter(
        self,
        phases: np.ndarray = None,
        antennas_space: float = None,
        antennas_population: int = None,
        tag_location: np.ndarray = None,
        x_err_range: float = None,
        y_err_range: float = None,
        initial_value: np.ndarray = None,
        is_noise_included: bool = None,
        initial_n: np.ndarray = None,
    ):
        if phases is not None:
            self.phases = phases
        if antennas_space != None:
            self.antennas_space = antennas_space
        if antennas_population != None:
            self.antennas_population = antennas_population
        if x_err_range != None:
            self.x_err_range = x_err_range
        if y_err_range != None:
            self.y_err_range = y_err_range
        if tag_location is not None:
            self.tag_location = tag_location
        if initial_value is not None:
            self.initial_value = initial_value
        if is_noise_included != None:
            self.is_noise_included = is_noise_included
        if initial_n is not None:
            self.initial_n = initial_n
        if not self.is_noise_included:
            self.initial_n = np.full(antennas_population, -1)

        for var in self.__model.component_objects(Var):
            self.__model.del_component(var)
        for set in self.__model.component_objects(Set):
            self.__model.del_component(set)
