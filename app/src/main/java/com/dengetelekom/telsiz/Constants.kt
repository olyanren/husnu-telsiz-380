package com.dengetelekom.telsiz

class Constants {
    companion object {
        val CLIENT_ID: Int = 3
        val CLIENT_SECRET = "eS5at8aLSEVhpFfYAiTA5RHCjP93tyxzBaDVXyIR"
        val GRANT_TYPE = "password"
        var ACCESS_TOKEN: String = ""
        var COMPANY_NAME: String = ""
        var BARCODE_READER_ACTIVE: Boolean = true
        var NFC_READER_ACTIVE: Boolean = true
        var IS_CHECKIN_AVAILABLE: Boolean = false
        var IS_NOTIFICATON_AVAILABLE: Boolean = false
        //const val API_BASE_URL: String = "http://demo.dengetelekom.com/"
        const val API_BASE_URL: String = "http://10.0.2.2:8000/"
        const val API_URL: String = API_BASE_URL + "api/v1/"
        const val SHOW_UPLOAD_PHOTO: Boolean = true

    }

}