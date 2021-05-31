package sigil

typealias Val = Value

/*
* Token class used as the return type of `lex(String)`
* which is then parsed into a tree of Expressions.
* */
sealed class Token {
    object Fn : Token() {
        override fun toString(): String = "Fn"
    }

    object Is : Token() {
        override fun toString(): String = "Is"
    }

    object If : Token() {
        override fun toString(): String = "If"
    }

    object Head : Token() {
        override fun toString(): String = "Head"
    }

    object Tail : Token() {
        override fun toString(): String = "Tail"
    }

    object Fuse : Token() {
        override fun toString(): String = "Fuse"
    }

    object Pair : Token() {
        override fun toString(): String = "Pair"
    }

    object Litr : Token() {
        override fun toString(): String = "Litr"
    }

    object Str : Token() {
        override fun toString(): String = "Str"
    }

    object Words : Token() {
        override fun toString(): String = "Words"
    }

    object Input : Token() {
        override fun toString(): String = "Input"
    }

    object Print : Token() {
        override fun toString(): String = "Print"
    }

    object Add : Token() {
        override fun toString(): String = "Add"
    }

    object Neg : Token() {
        override fun toString(): String = "Neg"
    }

    object Mul : Token() {
        override fun toString(): String = "Mul"
    }

    object Div : Token() {
        override fun toString(): String = "Div"
    }

    object Rem : Token() {
        override fun toString(): String = "Rem"
    }

    object Eq : Token() {
        override fun toString(): String = "Eq"
    }

    object Less : Token() {
        override fun toString(): String = "Less"
    }

    object LessEq : Token() {
        override fun toString(): String = "LessEq"
    }

    object Indent : Token() {
        override fun toString(): String = "Indent"
    }

    data class Ident(val i: String) : Token()
    data class Value(val v: Val) : Token()
}

/*
* Recursively defined `Expr` data type is what Values are combined into.
* They are made up of native and nonnative function calls.
* */
sealed class Expr {
    data class If(val cond: Expr, val t: Expr, val f: Expr) : Expr()
    data class Head(val list: Expr) : Expr()
    data class Tail(val list: Expr) : Expr()
    data class Fuse(val x: Expr, val y: Expr) : Expr()
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