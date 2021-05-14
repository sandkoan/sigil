data class Func(val args: ArrayList<String>, val expr: Expr)

fun input(msg: String): Value {
    print(msg)
    System.out.flush()

    val input = readLine()!!.replace("\n", "")
    return Value.Str(input)
}

fun eval(expr: Expr, funcs: HashMap<String, Func>, args: ArrayList<Value>): Value {
    when (expr) {
        is Expr.If -> {
            if (eval(expr.cond, funcs, args) == Value.Bool(true)) {
                eval(expr.t, funcs, args)
            } else {
                eval(expr.f, funcs, args)
            }
        }
        is Expr.Eq -> Value.Bool(
            eval(expr.x, funcs, args) == eval(expr.y, funcs, args)
        )
        is Expr.Add -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n + b.n)
            else if (a is Value.Str && b is Value.Str) Value.Str(a.s + b.s)
            else Value.Null
        }

    }
}
























