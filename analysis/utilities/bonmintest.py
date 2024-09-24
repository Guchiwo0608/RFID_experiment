from pyomo.environ import *
from pyomo.opt import SolverFactory
import numpy as np

F = 5  # Foodの数
N = 3  # 栄養素の数
food_data = np.zeros((F, N))

# 食品に含まれる栄養素
food_data[0] = [1, 0, 0]  # food1の100gあたりの各栄養素
food_data[1] = [0, 1, 0]  # food2の100gあたりの各栄養素
food_data[2] = [0, 0, 1]  # food3の100gあたりの各栄養素
food_data[3] = [3, 6, 1]  # food4の100gあたりの各栄養素
food_data[4] = [1, 2, 1]  # food5の100gあたりの各栄養素

# 摂取量の上下限
nutrient_max = [10, 20, 5]  # 各栄養素の一日の最大値
nutrient_min = [3, 6, 2]  # 各栄養素の一日の最小値


M = ConcreteModel("Bonmin Test")  # モデル
M.foods = Set(initialize=range(F))  # 変数の数
M.var = Var(
    M.foods, within=NonNegativeIntegers
)  # 最適化対象の変数定義：非負整数NonNegativeIntergersを指定。


# 制約条件を返す関数
def _con1(model, foods, nutrient):
    return (
        nutrient_min[nutrient],
        sum(food_data[food][nutrient] * model.var[food] for food in foods),
        nutrient_max[nutrient],
    )


# 制約条件
M.constraints = ConstraintList()
for nutrient in range(N):
    M.constraints.add(_con1(M, M.foods, nutrient))


# 目的関数を返す関数
def obj_rule(model):
    return sum((1 - 100 ** (-model.var[food])) for food in model.foods)


# 最小化問題として目的関数を設定
M.value = Objective(rule=obj_rule, sense=minimize)

# ソルバーとしてBonminを指定
opt = SolverFactory("Couenne", executable="./bonmin-win64_1.8.8fromAMPLcom/bonmin")

# オプションの設定 https://www.coin-or.org/Bonmin/option_pages/options_list_bonmin.html
# bonmin_option = {
#     "bonmin.algorithm": "B-BB",  # B-BB,B-OA,B-QG,B-Hyb,B-Ecp,B-iFP,Cbc_Par
#     # "bonmin.pump_for_minlp":"yes", # whether to run the feasibility pump heuristic for MINLP
#     "bonmin.random_generator_seed": -1,  # -1 sets seeds to time since Epoch/ option for B-BB
#     # "bonmin.time_limit":10,
#     # "bonmin.cutoff":8,
#     "halt_on_ampl_error": "yes",
# }

# 最適化モデルの確認
print(M.constraints.pprint())

# 実行
result = opt.solve(M, tee=True)

# 結果の表示
M.display()

# 結果の確認
condition = result["Solver"][0]["Termination condition"]
print(condition)  # optimal=最適化できた。infeasible=解無し

# 結果の抽出
if condition == "optimal":
    print("選択した食品の種類:{}種類".format(round(M.value())))

    print("選択した食品と個数")
    for f, food in zip(M.var, range(F)):
        var = M.var[f]()
        print("food{}:{}個".format(f, var))

    print("取得できる栄養素（下限～上限）")
    for const in M.component_objects(Constraint):
        for c in const:
            print(
                "栄養素{}={}({}~{})".format(
                    c,
                    M.constraints[c](),
                    value(M.constraints[c].lower),
                    value(M.constraints[c].upper),
                )
            )
