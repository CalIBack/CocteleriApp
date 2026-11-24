# CocktailApp 🍹

LINK: https://youtube.com/shorts/MlvVwwua0Dw?feature=share

Aplicación Android desarrollada en **Kotlin** que permite explorar un catálogo de cócteles y bebidas, consumiendo la API pública y gratuita **[TheCocktailDB](https://www.thecocktaildb.com/api.php)**.

## Funcionalidades

- **Búsqueda por nombre:** buscador de cócteles con los resultados en una lista con foto, nombre y categoría.
- **Filtro por ingrediente:** menú desplegable con todos los ingredientes de la API (se puede escribir para filtrar).
- **Detalle:** foto, etiquetas (categoría, tipo y vaso), ingredientes con sus medidas e instrucciones.
- **Estados claros:** indicador de carga, mensajes de "sin resultados" y errores con botón **Reintentar** (distingue sin conexión, error del servidor y respuesta inválida).
- **Rotación sin pérdidas:** al girar el celular no se pierde la búsqueda ni se repiten las llamadas.

## Arquitectura: MVVM + repositorio

```
Fragment  ──observa──▶  ViewModel (StateFlow<EstadoUi>)  ──pide──▶  CoctelRepositorio  ──▶  CoctelApi (Retrofit)  ──▶  TheCocktailDB
```

- **Fragments**: solo dibujan el estado y avisan las acciones del usuario.
- **ViewModels**: guardan el estado (`Cargando` / `Exito` / `Error`) y sobreviven a la rotación.
- **Repositorio**: única puerta a los datos; limpia los `null` de la API.
- **Retrofit + Gson**: hacen las peticiones y convierten el JSON.

## Tecnologías utilizadas

- **Kotlin**
- **MVVM**: ViewModel + StateFlow + `EstadoUi` (sealed interface)
- **Retrofit** + **Gson**: consumo de la API REST
- **Coroutines**: llamadas asincrónicas (`viewModelScope`, `repeatOnLifecycle`)
- **ViewBinding**: acceso tipado a las vistas
- **Navigation Component**: una sola Activity con Fragments
- **RecyclerView** con **ListAdapter + DiffUtil**
- **Glide**: carga de imágenes
- **Material 3**: TextInputLayout, menú desplegable, Chips, CardView, indicador de carga
- **JUnit**: pruebas unitarias del modelo y del repositorio

## Estructura del proyecto

```
com.example.cocktailapp
├── MainActivity.kt                     # Activity contenedora (NavHost + Toolbar)
├── datos/
│   ├── modelo/Coctel.kt                # Coctel, RespuestaCocteles, Ingrediente
│   ├── red/CoctelApi.kt                # Endpoints de la API
│   ├── red/ClienteRetrofit.kt          # Cliente Retrofit (singleton)
│   └── repositorio/CoctelRepositorio.kt
└── ui/
    ├── comun/                          # EstadoUi, errores, vista de mensajes
    ├── lista/                          # ListaFragment, ListaViewModel, CoctelAdapter
    └── detalle/                        # DetalleFragment, DetalleViewModel
```

Los cambios respecto de la primera versión están explicados en [CAMBIOS.md](CAMBIOS.md).

## API utilizada

- `search.php?s={nombre}` — búsqueda de cócteles por nombre
- `lookup.php?i={id}` — detalle completo de un cóctel por ID
- `filter.php?i={ingrediente}` — cócteles que llevan un ingrediente
- `list.php?i=list` — lista de todos los ingredientes

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle.
4. Ejecutar en un emulador o dispositivo físico con conexión a internet.

## Requisitos

- Android Studio actualizado
- SDK mínimo: API 24
- Conexión a internet (la app consume datos en vivo desde TheCocktailDB)
