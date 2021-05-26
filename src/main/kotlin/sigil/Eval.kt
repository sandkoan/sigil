package sigil

/*
* Eval function composed of a single when expression that recursively evaluates a given expression, given the arguments
* of a function and a map of the other functions and arguments called by the presently evaluated function call.
* Relatively simple, though there are some unique functions, like `Head` and `Tail` (which return the Head and Tail of a list,
* respectively), as well as `Fuse`, which combines Lists and Strs.
* */
fun eval(expr: Expr, funcs: Map<String, Func>, args: List<Value>): Value {
    return when (expr) {
        is Expr.If -> if (eval(expr.cond, funcs, args) == Value.Bool(true)) {
            eval(expr.t, funcs, args)
        } else {
            eval(expr.f, funcs, args)
        }
        is Expr.Eq -> Value.Bool(eval(expr.x, funcs, args) == eval(expr.y, funcs, args))
        is Expr.Add -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n + b.n)
            else if (a is Value.Str && b is Value.Str) Value.Str(a.s + b.s)
            else Value.Null
        }
        is Expr.Neg -> when (val y = eval(expr.n, funcs, args)) {
            is Value.Num -> Value.Num(-y.n)
            else -> Value.Null
        }
        is Expr.Mul -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n * b.n)
            else Value.Null
        }
        is Expr.Div -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n / b.n)
            else Value.Null
        }
        is Expr.Rem -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n % b.n)
            else Value.Null
        }
        is Expr.Less -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Bool(a.n < b.n)
            else if (a is Value.Str && b is Value.Str) Value.Bool(a.s < b.s)
            else Value.Null
        }
        is Expr.LessEq -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Bool(a.n <= b.n)
            else if (a is Value.Str && b is Value.Str) Value.Bool(a.s <= b.s)
            else Value.Null
        }
        is Expr.Head -> when (val l = eval(expr.list, funcs, args)) {
            is Value.List -> l.items.elementAtOrElse(0) { Value.Null }
            is Value.Str -> if (l.s.isNotEmpty()) Value.Str(l.s[0].toString()) else Value.Null
            else -> Value.Null
        }
        is Expr.Tail -> when (val l = eval(expr.list, funcs, args)) {
            is Value.List -> if (l.items.drop(1).isNotEmpty()) Value.List(l.items.drop(1)
                .toMutableList()) else Value.Null
            is Value.Str -> if (l.s.substring(1).isNotEmpty()) Value.Str(l.s.substring(1)) else Value.Null
            else -> Value.Null
        }
        is Expr.Fuse -> {
            val x = eval(expr.x, funcs, args)
            val y = eval(expr.y, funcs, args)
            if (x is Value.List && y is Value.List) Value.List((x.items + y.items).toMutableList())
            else if (x is Value.List) Value.List((x.items + y).toMutableList())
            else if (y is Value.List) Value.List((mutableListOf(x) + y.items).toMutableList())
            else Value.List(mutableListOf(x, y))
        }
        is Expr.Pair -> Value.List(arrayListOf(eval(expr.f, funcs, args), eval(expr.p, funcs, args)))
        is Expr.Call -> {
            val f = funcs[expr.f]
            if (f != null) eval(f.expr, funcs, expr.params.map { eval(it, funcs, args) }) else Value.Null
        }
        is Expr.Words -> when (val s = eval(expr.x, funcs, args)) {
            is Value.Str -> Value.List(words(s.s).map { Value.Str(it) }.toMutableList())
            else -> Value.Null
        }
        is Expr.Litr -> when (val s = eval(expr.x, funcs, args)) {
            is Value.Str -> Value.of(s.s).getOrDefault(Value.Null)
            else -> Value.Null
        }
        is Expr.Input -> input(eval(expr.x, funcs, args).toString())
        is Expr.Print -> {
            val v = eval(expr.x, funcs, args)
            println(v.toString())
            v
        }
        is Expr.Str -> Value.Str(eval(expr.x, funcs, args).toString())
        is Expr.Value -> expr.v
        is Expr.Local -> args.getOrElse(expr.idx) { Value.Null }
    }
}
