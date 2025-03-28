package com.affan.i220916

import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object AccessToken {

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String? {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"i220916\",\n" +
                    "  \"private_key_id\": \"72d72649f02680be09a2451b6b7d669f4cc1c993\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCfZkfhzd5XpM2w\\nv15w6rfEbi9TW1vIq3bBYL/uLo2u+fIkOtbRwNcS2vR8cTs0c6q7W5kstkowfRh7\\nfedYG63JWER2HRSd49GPBs9dM0DzSL3un0/EyezaLLNS5lFtPqJJRjRcJz6LZJ0V\\nNEqMUEL5PSVrzhbuKXHrvmSf7TTK0fviC6i1nB2dsDsHwKorHD3v1gKS8r2r7HXr\\nsElHQmQ3p56jSE8ilpodhAqBU6Gg3r1Y/4OXCQCSQ9ErO3S27cpsyW6P7whIxUKh\\n6EUmtGUbIkKP2U7v2z0TTC6fblvGRN2AwgdqyShSflPopy3SK1k41cqdIsr17i72\\nJV+A8QtlAgMBAAECggEAASWTu9Cm2luRrUVbMRXHN68RQRjFqsZtBswi5VxJYapj\\nrxYLSJwKEBUqMCECa4ivgESS8Arpsrqbq/vGZ9BfKjUr3Outo8spTU2fvUxqmbV6\\nxFY6FTDctt8edSi6Cj6qnTtXkhgoL4mMRcRICwVizfc+Jy3m5VTAAJGwmKP0vsbM\\nDRkDCkI27ShIqkJ7crUH1Y/E02rdBJ+gbAjPq3JLoxfjz0Ql/CFyZbz5hGkmp7yV\\nED7VzbiSE0H0wbV9vOgqR7EjGiAAMxHHYM8Jt3t48qiiIXBVWFGewEIflIXXY5qH\\nScWwJdZrr0FNS3lfX00pMjt3cmZ7bSyx7c73R5Ij6wKBgQDeT409PmVka3H3dfwt\\n96EkmrVBpR5dsX6Pvsnx97ixr6g5kq8mfA7PYZ3DrMBFakWpFydNLBHGPrupNacK\\nYNrF9++1bF0ZGFxiKPawXxARzaDHc2HxIT1hh0eIkNpujJMz22KfC4fGNmzSoEV3\\n5pdgE/sNfiyuuk2I7YeyRR8iVwKBgQC3jh2zQmjYfD3RQVTL6WFJVVfsiQKPfkl/\\nMwRdJKn1Gk63x5g1Qh9CWoKBzW0L6v/OCfnZRZIUJEdEGl2bQcQwPclGb0Fm3vqv\\n1L9YOupnUhOl9wW8eAkNSu6/OYNqIh7anplGbIU9XYA9Q6z+CbGQrzfBzupCbPhH\\nSY4npgKCowKBgAYaOu9OciwSgvtfAsAkRwkhMg6OQMojoCzD+Pkqaqh+AOkrrKEq\\n0KFCtbj0WgbNxwHAP/TvW8GMYUxgy6gORpMenjbltZyFvat4ImVvBprmgR5YfXzI\\nA85Uj1MhXs2gED5hLFDfWSfIwK37dky3SLp7ce3J4+Ib/RrgIlRzb0pbAoGBAK7U\\nqClTubVpMSAcz/2g/s7wgNyE63FtYefL2f41q8QPGKcnbyxbHJcAyomD9reBT8EZ\\n82YlQ6v3mgGd80ar74fbcdiqo2quO6w6QT5yb02V+az8ifQniMemdZyh1S/D7Atj\\nQK65MndulXTE/wLAjOY4Xi01Ph+i1yBfSQ0uq/ltAoGAMOtnUT+wfx4StNHn1T7z\\n+yG8lqnKUqpmSPxuaeJGckhB5+05Kp/ZiC2A+ZOaG4TfgzHB8f82wEn19An7aOVU\\nUhtI/B1yIIrUsrHGnxqQXuf/j4ZKdRsYgM6mzotTMk7+Nqrqf0A2TYBDTplzeoiq\\nTWgxrTbEl+mdNtrlvWHwlMU=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-fbsvc@i220916.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"106272945431869731685\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40i220916.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))

            googleCredentials.refresh()

            return googleCredentials.accessToken.tokenValue
        }catch(e: IOException)
        {
            return null
        }
    }

}