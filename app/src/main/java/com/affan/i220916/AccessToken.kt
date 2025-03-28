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
                    "  \"project_id\": \"i220799\",\n" +
                    "  \"private_key_id\": \"f8c5863cedfcfa893133615218317b3e3c2e90dc\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQjqgAGzbNninE\\nZ2XMQ/TgPai9IRl9MtWSRlMujhp9z+i8JrvgSp3IPCNda8VyQoxTBj4tGaN0tJob\\n28Ywd3kF4NT+0La6SMhBiy9f9WlilWR+S4DoDnA94GLq0X146tkQ9ExxpHacNUjR\\nkRX0ZjqzTwlhk+guW4DJxv2xWHSoKR6IJESUcLu9C6E7e8uzOfdEHSQLcrGGBsvC\\nRcA9U8l/Lbtq4uyuehVHID0WvuTu7wuh3LJ2SAG/fovlw5i1gPtmWVS+o+081nCO\\nSorS/Py+PPqPfzxxHXF7npARDCrWd+xCoaDDNHNyz7CyWQKQZ+/GuWL81FXIOzr5\\nkK/cXFqrAgMBAAECggEACcATQI1wyiX15vXkJuI/HRl933UFp4idhcHRa+++qfoO\\nxDfQCk0CsXYb0bDhXum7n0n8FHytgZ73vqWU+VOhEtMkarZpfxZF85uGdPBYzPDB\\nzuuJG53hH/IsLCWmCety+9dXrcfDDpnzMQCaYTWYA7fgmkC6/I17LDNDH1OVosOo\\nI16SCwiLelPCh1j0Mkj1SZqQm6cn+ejhQKgWQbkU57qSHup//AmJ9W4QVgQ2Gyqb\\nezxXcpzmHihm0sIBUDQYPLQlZFjAFH0pMoT5qQCCBCK+EIVmeei3VmLKtDmZcU1P\\nGgMy1+0xwJlcnqivpJU1gFoSnzmLWRopXohiV7ABUQKBgQDIhJyNxY4mdlIBTtG+\\n5N/aVQiDlMuAV8NWJDune8dezl/cPLUa2I36rtr5Pu51tcOhZ8cqyDmwrYELXDho\\nfcEghDmdGrYPjspUpOmRYOgjV8DzaCGZakyp5Ii3m3WAd2hT03rUzMgbtQUt1fWE\\nTU3mNOtXhsBBLEVk4El6Hc+TMwKBgQC4jiYyql2rCtqrEhTG4g7qykXmARp+USKr\\n2+VHAaN2e5Z1NGGO6CSPmwY2HO44LQZDdYmwR44Fo7f/2HKWeL7YUE9dqizu6FOs\\nq86vJyYfbe+m5T5fTNqvXk5+6I2PMOkReFver6BolMg3D1ez7oyTdftPM8AOmTV4\\nzVofIRsaqQKBgAe+b1leTs4XdbVNOPZ5WlfRPuHuBinvucCC57bhJeL4b2VPBuOB\\nh4It7JEBqC3tlh+OpXd2ghvbp4cryZXnfWTnFl6F4JAbQJ8iO079AhoTUoVT+TA2\\nlb1d8RW+MDyE4AeUvEKpofTH+eo6QCAa6C5Czu/zaUtbDVcG/m9z5UdXAoGAIkbk\\neUyDER1294boLPgXZSwkR0+1J/jRw7GXp9R6E6toimQsKmqbP8C/KpW8+NoD68uM\\nwxzHF+0MY4xD3AaZC3v/AZp1tfmYcN2ICafvwK6ecBTrvK99fky5r4BpqkqfPwtn\\npLhhxoq7lN5Yn6aSnWCwSEmIRi3WbJVXQ6jWa+ECgYEAmuRjLrE1i6HP6QxIcjEs\\nGs3+04VJsCPoGQEnC05iBqKW/TKLDntel5FAb9yqVKIt/Cw3aWymdhfuKynGrPfw\\nHIqTLSkV1CGF4w+gbVVGudaw++l2N0uCxy59SAWoc16Pa6BiKRMkxeyshSxWg9ml\\nLlw9aIclWzqLNVYLo1I6N8o=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-fbsvc@i220799.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"104143689502219817436\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40i220799.iam.gserviceaccount.com\",\n" +
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