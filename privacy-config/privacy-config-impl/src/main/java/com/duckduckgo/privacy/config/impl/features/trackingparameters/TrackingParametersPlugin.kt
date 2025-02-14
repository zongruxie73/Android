/*
 * Copyright (c) 2022 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.privacy.config.impl.features.trackingparameters

import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.privacy.config.api.PrivacyFeatureName
import com.duckduckgo.privacy.config.api.PrivacyFeaturePlugin
import com.duckduckgo.privacy.config.impl.features.privacyFeatureValueOf
import com.duckduckgo.privacy.config.store.*
import com.duckduckgo.privacy.config.store.features.trackingparameters.TrackingParametersRepository
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TrackingParametersPlugin @Inject constructor(
    private val trackingParametersRepository: TrackingParametersRepository,
    private val privacyFeatureTogglesRepository: PrivacyFeatureTogglesRepository,
) : PrivacyFeaturePlugin {

    override fun store(featureName: String, jsonString: String): Boolean {
        val privacyFeature = privacyFeatureValueOf(featureName) ?: return false
        if (privacyFeature.value == this.featureName) {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<TrackingParametersFeature> =
                moshi.adapter(TrackingParametersFeature::class.java)

            val exceptions = mutableListOf<TrackingParameterExceptionEntity>()
            val parameters = mutableListOf<TrackingParameterEntity>()

            val trackingParametersFeature: TrackingParametersFeature? = jsonAdapter.fromJson(jsonString)

            trackingParametersFeature?.exceptions?.map {
                exceptions.add(TrackingParameterExceptionEntity(it.domain, it.reason.orEmpty()))
            }

            trackingParametersFeature?.settings?.parameters?.map {
                parameters.add(TrackingParameterEntity(it))
            }

            trackingParametersRepository.updateAll(exceptions, parameters)
            val isEnabled = trackingParametersFeature?.state == "enabled"
            privacyFeatureTogglesRepository.insert(PrivacyFeatureToggles(this.featureName, isEnabled, trackingParametersFeature?.minSupportedVersion))
            return true
        }
        return false
    }

    override val featureName: String = PrivacyFeatureName.TrackingParametersFeatureName.value
}
