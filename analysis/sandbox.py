import sympy as sp
import numpy as np

r, t, l, lam = sp.symbols('r, t, l, lam')

# l = 0.25
# lam = 0.3257

sol = sp.solve(sp.sqrt(r**2 + (l/2)**2 + r*l*sp.sin(t))-sp.sqrt(r**2 + (l/2)**2 - r*l*sp.sin(t)) - lam/2, t)

for i in sol:
    print(i)

# for i in range(0, 11):
#     t = sol[3].subs(r,i)
#     print(t / 2 / np.pi * 360)