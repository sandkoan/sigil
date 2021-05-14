typealias Intrins = Intrinsic
typealias Val = Value

sealed class Token {
    data class Intrinsic(val i: Intrins) : Token()
    data class Ident(val id: String) : Token()
    data class Value(val v: Val) : Token()
}

sealed class Expr {
    data class If(val cond: Expr, val t: Expr, val f: Expr) : Expr()
    data class Head(val h: Expr) : Expr()
    data class Tail(val t: Expr) : Expr()
    data class Fuse(val h: Expr, val e: Expr) : Expr()
    data class Pair(val f: Expr, val p: Expr) : Expr()
    data class Litr(val s: Expr) : Expr()
    data class Str(val s: Expr) : Expr()
    data class Words(val s: Expr) : Expr()
    data class Input(val s: Expr) : Expr()
    data class Print(val s: Expr) : Expr()

    data class Eq(val x: Expr, val y: Expr) : Expr()
    data class Add(val x: Expr, val y: Expr) : Expr()
    data class Neg(val n: Expr) : Expr()
    data class Mul(val x: Expr, val y: Expr) : Expr()
    data class Div(val x: Expr, val y: Expr) : Expr()
    data class Rem(val x: Expr, val y: Expr) : Expr()
    data class Less(val x: Expr, val y: Expr) : Expr()
    data class LessEq(val x: Expr, val y: Expr) : Expr()

    data class Value(val v: Val) : Expr()
    data class Call(val s: String, val list: ArrayList<Expr>) : Expr()
    data class Local(val size: Int) : Expr()
}