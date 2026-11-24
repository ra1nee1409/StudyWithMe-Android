---
name: android-dev
description: "Production-grade Android app development guide by sickn33 covering native Java (XML Views, ViewBinding, LiveData, ViewModel) and Kotlin architectures. Prioritizes Java + XML and Retrofit when project uses Java instead of Jetpack Compose."
license: Apache-2.0
metadata:
  author: sickn33
  source: https://github.com/sickn33/agentic-awesome-skills
  category: mobile-development
---

# Android App Development Skill (by sickn33)

## Stack Priority & Core Directives

> [!IMPORTANT]
> **JAVA + XML DIRECTIVE:**
> If the project uses **Java + XML Views** (such as `ConstraintLayout`, `ViewBinding`, `RecyclerView`, `Navigation Component` with Fragments, `LiveData`, and `ViewModel`) instead of Jetpack Compose:
> - **STRICTLY USE JAVA + XML VIEWS**: Do NOT introduce or generate Jetpack Compose code. Keep the architecture and code style consistent with existing Java code.
> - Follow [Java Android Reference](references/java-android.md) as the primary implementation blueprint.
> - Use **ViewBinding** for view access (ensure `binding = null` in Fragment `onDestroyView()`).
> - Use `ConstraintLayout` with theme tokens (`?attr/...`) in XML.
> - Manage background operations via `ExecutorService` / Room threading / Retrofit async callbacks.

## Detailed Guide

Read [the detailed guide](references/detailed-guide.md) before executing this skill. It retains the complete procedure and reference material across architecture, UI, error handling, testing, and release pipelines.

For stack-specific deep dives:
- **Java + XML Views (Primary for Java projects):** [references/java-android.md](references/java-android.md)
- **Native Kotlin + Compose:** [references/native-android.md](references/native-android.md)

## When to Use This Skill

- Deciding on or adhering to tech stack (§1 Stack Selection)
- Setting up or extending project architecture (§2 Architecture - Clean Architecture, MVVM)
- Designing UI, layouts, or XML screens (§3 UI & Design - ConstraintLayout, ViewBinding, Design Tokens)
- Enforcing code quality, null safety, memory leak prevention (Best Practices)
- Error handling, Network resilience, Result patterns (§5 Error Handling)
- Unit and UI testing strategy (§6 Testing - JUnit, Mockito, Espresso)
- Build, Gradle, ProGuard/R8 configurations (§7 Build & Release)
- Performance and memory leak prevention (§8 Performance - LeakCanary, background tasks)
- Systematic debugging and bug fixing (§9 Debugging)

## Architecture Overview (Java + XML Stack)
```
app/
├── ui/
│   └── feature/
│       ├── FeatureActivity.java / FeatureFragment.java
│       └── FeatureAdapter.java (ListAdapter + DiffUtil)
├── viewmodel/
│   └── FeatureViewModel.java (LiveData + UiState)
├── data/
│   ├── remote/
│   │   ├── ApiService.java (Retrofit)
│   │   ├── ApiClient.java (OkHttpClient + Gson)
│   │   └── dto/
│   └── local/
│       ├── AppDatabase.java (Room)
│       ├── FeatureDao.java
│       └── entity/
└── repository/
    └── FeatureRepository.java
```
