package com.egoriku.mainscreen.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.egoriku.core.di.findDependencies
import com.egoriku.core.di.utils.INavigationHolder
import com.egoriku.core.feature.IFeatureProvider
import com.egoriku.ladyhappy.arch.activity.BaseActivity
import com.egoriku.ladyhappy.extensions.*
import com.egoriku.ladyhappy.navigation.navigator.platform.ActivityScopeNavigator
import com.egoriku.mainscreen.BuildConfig
import com.egoriku.mainscreen.R
import com.egoriku.mainscreen.di.MainActivityComponent
import com.egoriku.mainscreen.presentation.dynamicfeature.DynamicFeatureViewModule
import com.egoriku.mainscreen.presentation.screen.*
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_content.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import javax.inject.Inject

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val featureProvider: IFeatureProvider by inject()
    private val featureViewModule: DynamicFeatureViewModule by viewModel()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigatorHolder: INavigationHolder

    private lateinit var viewModel: MainActivityViewModel

    private val navigator = ActivityScopeNavigator(this, R.id.container)

    override fun injectDependencies() = MainActivityComponent.init(findDependencies()).inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasM()) {
            window.statusBarColor = Color.BLACK
        }

        setSupportActionBar(toolbarMainActivity)

        viewModel = injectViewModel(viewModelFactory)

        viewModel.screenTitle.observe(this, Observer {
            headerBarLogoText.setText(it)
        })

        when (savedInstanceState) {
            null -> viewModel.replaceWith(CatalogScreen(featureProvider))
            else -> with(savedInstanceState.getInt(KEY_SELECTED_MENU_ITEM)) {
                bottomNavigation.selectedItemId = this
            }
        }

        with(bottomNavigation) {
            setOnNavigationItemSelectedListener { item ->
                consume {
                    mapItemIdToScreen(item.itemId)
                }
            }

            setOnNavigationItemReselectedListener {}
        }

        if (BuildConfig.DEBUG) {
            with(createPostButton) {
                visible()
                setOnClickListener {
                    featureViewModule.installDynamicFeature(getString(R.string.title_post_creator))
                }

                featureViewModule.installStatus.observe(this@MainActivity) {
                    when (it) {
                        true -> onSuccessfulLoad()
                        false -> toast("error")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    //TODO use navigation library approach
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
            return
        }

        viewModel.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_MENU_ITEM, bottomNavigation.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.installActivity(this)
    }

    private fun mapItemIdToScreen(@IdRes menuItemId: Int) {
        when (menuItemId) {
            R.id.menuLanding -> viewModel.replaceWith(LandingScreen(featureProvider))
            R.id.menuPhotoReport -> viewModel.replaceWith(PhotoReportScreen(featureProvider))
            R.id.menuCatalog -> viewModel.replaceWith(CatalogScreen(featureProvider))
            R.id.menuSettings -> viewModel.replaceWith(SettingsScreen(featureProvider))
        }
    }

    private fun onSuccessfulLoad() {
        viewModel.navigateTo(PostCreatorScreen())
    }

    companion object {
        private const val KEY_SELECTED_MENU_ITEM = "selected_item"
    }
}