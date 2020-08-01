package lk.prathieshna.bookfinder.domain.api

import lk.prathieshna.bookfinder.domain.local.ApiError

data class ApiErrorMessage(
    var error: ApiError
)