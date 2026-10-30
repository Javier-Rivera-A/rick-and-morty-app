package com.example.rickandmortyapp.data.remote

// La API referencia otros recursos con su URL (por ejemplo .../api/location/3).
// Esta función obtiene el id que está al final; devuelve null si la URL está vacía
fun String.idFromUrl(): Int? = substringAfterLast('/').toIntOrNull()
