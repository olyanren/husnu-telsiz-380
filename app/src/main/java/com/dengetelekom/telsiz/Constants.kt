package com.dengetelekom.telsiz

class Constants {
    companion object {
        val CLIENT_ID: Int = 3
        val CLIENT_SECRET = "eS5at8aLSEVhpFfYAiTA5RHCjP93tyxzBaDVXyIR"
        val GRANT_TYPE = "password"
        var ACCESS_TOKEN: String = ""
        var BARCODE_READER_ACTIVE: Boolean = true
        var NFC_READER_ACTIVE: Boolean = true
        const val API_BASE_URL: String = "http://cyguvenlik.dengetelekom.com/"
        const val API_URL: String = API_BASE_URL + "api/v1/"
        const val SHOW_UPLOAD_PHOTO: Boolean = true

    }

}