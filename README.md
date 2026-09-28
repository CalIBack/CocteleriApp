# CocktailApp 🍹

Aplicación Android desarrollada en **Kotlin** que permite explorar un catálogo de cócteles y bebidas, consumiendo la API pública y gratuita **[TheCocktailDB](https://www.thecocktaildb.com/api.php)**.

## Funcionalidades

- **Pantalla principal:** buscador de cócteles por nombre, con los resultados mostrados en una lista (RecyclerView) con imagen y nombre de cada bebida.
- **Pantalla de detalle:** al seleccionar un cóctel, se muestra su foto en alta resolución, la lista completa de ingredientes con sus cantidades, y las instrucciones de preparación paso a paso.
- Manejo de estados de carga y errores de conexión.
- Diseño propio con tema de colores personalizado y tarjetas (Material Design).

## Tecnologías utilizadas

- **Kotlin**
- **Retrofit** + **Gson** — consumo de la API REST
- **Coroutines** — llamadas de red asincrónicas
- **Navigation Component** — navegación entre pantallas mediante Fragments
- **Glide** — carga y transformación de imágenes
- **Material Components** — CardView, Toolbar, tema visual

## Estructura del proyecto

```
com.example.cocktailapp
├── data/
│   ├── CocktailApi.kt        # Interfaz de endpoints de la API
│   ├── CocktailModels.kt     # Modelos de datos (data classes)
│   └── RetrofitClient.kt     # Cliente Retrofit (singleton)
├── DrinkAdapter.kt           # Adapter del RecyclerView
├── DrinkListFragment.kt      # Pantalla principal (búsqueda + lista)
├── DrinkDetailFragment.kt    # Pantalla de detalle
└── MainActivity.kt           # Activity contenedora (NavHost)
```

## API utilizada

- `search.php?s={nombre}` — búsqueda de cócteles por nombre
- `lookup.php?i={id}` — detalle completo de un cóctel por ID

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle.
4. Ejecutar en un emulador o dispositivo físico con conexión a internet.

## Requisitos

- Android Studio actualizado
- SDK mínimo: API 24
- Conexión a internet (la app consume datos en vivo desde TheCocktailDB)
