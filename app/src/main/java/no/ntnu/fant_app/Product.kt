package no.ntnu.fant_app

data class Product (val id: Int, val title: String, val description: String, val price: Int, val seller: String, val photos: MutableList<String>)