# Rick and Morty App

Aplicación Android hecha con Jetpack Compose para la actividad de consumo de APIs REST del curso Construcción de Aplicaciones Móviles (Universidad del Quindío).

Consume la [Rick and Morty API](https://rickandmortyapi.com/documentation) con Retrofit y permite:

- Ver la lista de personajes con scroll infinito (paginación de la API).
- Buscar por nombre y filtrar por estado y género.
- Ver el detalle de cada personaje con su ubicación actual y los episodios en los que aparece.
- Ver si el dispositivo está en línea o sin conexión.

## Endpoints usados

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/character?page=&name=&status=&gender=` | Lista paginada y filtros |
| GET | `/character/{id}` | Detalle del personaje |
| GET | `/episode/[ids]` | Episodios del personaje |
| GET | `/location/{id}` | Ubicación actual del personaje |

## Tecnologías

Kotlin, Jetpack Compose, Material 3, Retrofit + Gson, Hilt, Navigation Compose, Coil y corrutinas con StateFlow.

## Estructura

```
app/src/main/java/com/example/rickandmortyapp
├── core          # componentes y utilidades compartidas
├── data          # Retrofit (ApiService, DTOs) e implementación de repositorios
├── di            # módulos de Hilt
├── domain        # modelos, interfaces de repositorios y casos de uso
├── features      # pantallas y ViewModels
└── navigation    # rutas y NavHost
```

## Cómo ejecutarla

Abrir el proyecto en Android Studio y ejecutar el módulo `app` en un emulador o dispositivo con Android 9 o superior. La API es pública y no necesita llave.
