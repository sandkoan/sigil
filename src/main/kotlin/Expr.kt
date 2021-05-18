typealias Val = Value

sealed class Token {
    object Fn : Token()
    object Is : Token()

    object If : Token()
    object Head : Token()
    object Tail : Token()

    object Fuse : Token()
    object Pair : Token()
    object Litr : Token()

    object Str : Token()
    object Words : Token()
    object Input : Token()
    object Print : Token()

    object Add : Token()
    object Neg : Token()
    object Mul : Token()
    object Div : Token()
    object Rem : Token()
    object Eq : Token()
    object Less : Token()
    object LessEq : Token()

    data class Ident(val i: String) : Token()
    data class Value(val v: Val) : Token()
}

sealed class Expr {
    data class If(val cond: Expr, val t: Expr, val f: Expr) : Expr()
    data class Head(val list: Expr) : Expr()
    data class Tail(val list: Expr) : Expr()
    data class Fuse(val y: Expr, val x: Expr) : Expr()
    data class Pair(val f: Expr, val p: Expr) : Expr()
    data class Litr(val x: Expr) : Expr()
    data class Str(val x: Expr) : Expr()
    data class Words(val x: Expr) : Expr()
    data class Input(val x: Expr) : Expr()
    data class Print(val x: Expr) : Expr()

    data class Eq(val x: Expr, val y: Expr) : Expr()
    data class Add(val x: Expr, val y: Expr) : Expr()
    data class Neg(val n: Expr) : Expr()
    data class Mul(val x: Expr, val y: Expr) : Expr()
    data class Div(val x: Expr, val y: Expr) : Expr()
    data class Rem(val x: Expr, val y: Expr) : Expr()
    data class Less(val x: Expr, val y: Expr) : Expr()
    data class LessEq(val x: Expr, val y: Expr) : Expr()

    data class Value(val v: Val) : Expr()
    data class Call(val f: String, val params: List<Expr>) : Expr()
    data class Local(val idx: Int) : Expr()
}