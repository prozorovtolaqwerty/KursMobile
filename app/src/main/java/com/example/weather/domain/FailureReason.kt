package com.example.weather.domain

enum class FailureReason {
    NETWORK,
    API_KEY,
    NOT_FOUND,
    DUPLICATE,
    UNKNOWN
}
