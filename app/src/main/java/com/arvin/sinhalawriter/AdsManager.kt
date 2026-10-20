package com.arvin.sinhalawriter

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayAdSize
import com.unity3d.mediation.LevelPlayConfiguration
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener

/** Android equivalent of the Flutter writer's banner and interstitial flow. */
class AdsManager(private val activity: Activity) {
    private val bannerId = activity.getString(R.string.levelplay_banner_ad_unit_id).trim()
    private val interstitialId = activity.getString(R.string.levelplay_interstitial_ad_unit_id).trim()

    var banner by mutableStateOf<LevelPlayBannerAdView?>(null)
        private set
    // Reserve the slot before the first request so a late ad cannot shift editable content.
    var showBannerSlot by mutableStateOf(bannerId.isNotEmpty())
        private set

    private var bannerLoadRequested = false
    private var interstitial: LevelPlayInterstitialAd? = null

    fun initialize() {
        if (bannerId.isEmpty()) {
            Log.w(TAG, "Set the LevelPlay banner ad unit ID in strings.xml to enable banner ads")
        }
        if (interstitialId.isEmpty()) {
            Log.w(TAG, "Set the LevelPlay interstitial ad unit ID in strings.xml to enable interstitial ads")
        }
        if (bannerId.isEmpty() && interstitialId.isEmpty()) return

        if (BuildConfig.DEBUG) LevelPlay.setAdaptersDebug(true)
        val request = LevelPlayInitRequest.Builder(activity.getString(R.string.levelplay_app_key)).build()
        LevelPlay.init(activity, request, object : LevelPlayInitListener {
            override fun onInitSuccess(configuration: LevelPlayConfiguration) {
                if (BuildConfig.DEBUG) {
                    LevelPlay.validateIntegration(activity)
                    LevelPlay.launchTestSuite(activity.applicationContext)
                }
                loadBanner()
                loadInterstitial()
            }

            override fun onInitFailed(error: LevelPlayInitError) {
                showBannerSlot = false
                Log.e(TAG, "LevelPlay initialization failed: ${error.errorMessage}")
            }
        })
    }

    private fun loadBanner() {
        if (bannerId.isEmpty()) return
        val config = LevelPlayBannerAdView.Config.Builder()
            .setAdSize(LevelPlayAdSize.BANNER)
            .setPlacementName("WRITER_BANNER")
            .build()
        bannerLoadRequested = false
        banner = LevelPlayBannerAdView(activity, bannerId, config).apply {
            setBannerListener(object : LevelPlayBannerAdViewListener {
                override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Banner loaded: $adInfo")
                }

                override fun onAdLoadFailed(error: LevelPlayAdError) {
                    Log.w(TAG, "Banner load failed: ${error.errorMessage}")
                }

                override fun onAdClicked(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Banner clicked: $adInfo")
                }

                override fun onAdExpanded(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Banner expanded: $adInfo")
                }

                override fun onAdCollapsed(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Banner collapsed: $adInfo")
                }

                override fun onAdLeftApplication(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Banner left application: $adInfo")
                }
            })
            // LevelPlay expects the view to be in its container before requesting an ad.
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    if (!bannerLoadRequested) {
                        bannerLoadRequested = true
                        loadAd()
                    } else {
                        resumeAutoRefresh()
                    }
                }

                override fun onViewDetachedFromWindow(view: View) {
                    pauseAutoRefresh()
                }
            })
        }
    }

    private fun loadInterstitial() {
        if (interstitialId.isEmpty()) return
        interstitial = LevelPlayInterstitialAd(interstitialId).apply {
            setListener(object : LevelPlayInterstitialAdListener {
                override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Interstitial ready: $adInfo")
                }

                override fun onAdLoadFailed(error: LevelPlayAdError) {
                    Log.w(TAG, "Interstitial load failed: ${error.errorMessage}")
                }

                override fun onAdDisplayed(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Interstitial displayed: $adInfo")
                }

                override fun onAdClicked(adInfo: LevelPlayAdInfo) {
                    Log.d(TAG, "Interstitial clicked: $adInfo")
                }

                override fun onAdClosed(adInfo: LevelPlayAdInfo) {
                    interstitial?.loadAd()
                }

                override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
                    Log.w(TAG, "Interstitial display failed: ${error.errorMessage}")
                    interstitial?.loadAd()
                }
            })
            loadAd()
        }
    }

    fun showInterstitial() {
        interstitial?.takeIf { it.isAdReady }?.showAd(activity)
    }

    fun destroy() {
        banner?.destroy()
        banner = null
        interstitial = null
    }

    private companion object {
        const val TAG = "SinhalaWriterAds"
    }
}
