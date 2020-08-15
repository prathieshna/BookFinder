package lk.prathieshna.bookfinder.domain.local

enum class Viewability(val value: String) {
    PARTIAL("PARTIAL"),
    ALL_PAGES("ALL_PAGES"),
    NO_PAGES("NO_PAGES"),
    UNKNOWN("UNKNOWN")
}