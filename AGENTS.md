# Project Directives for AI Agents

## Tech Stack & Architecture
- **Language:** Java (Java 11)
- **UI Toolkit:** Android XML Views (`ConstraintLayout`, `ViewBinding`, `Material Components`, `RecyclerView`)
- **Navigation:** Android Jetpack Navigation Component with Fragments (`res/navigation/nav_graph.xml`)
- **State & Lifecycle:** Android Architecture Components (`ViewModel`, `LiveData`)
- **Networking:** Retrofit + OkHttp (`Call<T>`, GsonConverterFactory)

> [!CAUTION]
> **STRICT PROHIBITION ON JETPACK COMPOSE:**
> This repository is built with **Java + XML Layouts**. 
> - **DO NOT** introduce or generate Jetpack Compose code, `@Composable` functions, or Compose dependencies.
> - Always use XML layouts (`res/layout/`) with **ViewBinding** (`FragmentXBinding`, `ActivityXBinding`).
> - Remember to clear binding references in Fragment `onDestroyView()` (`binding = null`) to avoid memory leaks.

## Priority Agent Skills
The following skills have been installed and vendorized for this project:
1. **[`android-dev`](.github/skills/android-dev/SKILL.md)** (by `sickn33`):
   - Consult [`references/java-android.md`](.github/skills/android-dev/references/java-android.md) for Java project structure, MVVM with LiveData/UiState, Room DB, Repository pattern, and XML best practices.
2. **[`retrofit-networking`](.github/skills/retrofit-networking/SKILL.md)** (by `new-silvermoon`):
   - Follow Retrofit best practices for Java (`Call<T>`, asynchronous `enqueue()`, `OkHttpClient` interceptors, logging, and error handling).
