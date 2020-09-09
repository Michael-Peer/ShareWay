package com.example.shareway

import android.app.Application
import com.example.shareway.persistence.ArticleDatabase
import com.example.shareway.repositories.ArticleRepository
import com.example.shareway.repositories.CategoryRepository
import com.example.shareway.viewmodels.ArticlesViewModel
import com.example.shareway.viewmodels.CategoriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainApp : Application() {


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApp)
            modules(koinModule)
        }
    }

    private val koinModule = module {

        single { ArticleDatabase.dbInstance(context = androidContext()) }

        single {
            CategoryRepository(
                get<ArticleDatabase>().articleDao(),
                get<ArticleDatabase>().categoryDao()
            )
        }

        single {
            ArticleRepository(
                get<ArticleDatabase>().articleDao()
            )
        }

        single { get<ArticleDatabase>().articleDao() }
        single { get<ArticleDatabase>().categoryDao() }


        viewModel {
            CategoriesViewModel(
                categoryRepository = get(),
                context = androidContext()
            )
        }

        viewModel {
            ArticlesViewModel(
                articleRepository = get()
            )
        }


    }
}