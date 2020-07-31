package lk.prathieshna.bookfinder.domain.local

enum class SearchFilter(val value: String) {
    PARTIAL("partial"),
    FULL("full"),
    FREE_EBOOKS("free-ebooks"),
    PAID_EBOOKS("paid-ebooks"),
    EBOOKS("ebooks")
}
