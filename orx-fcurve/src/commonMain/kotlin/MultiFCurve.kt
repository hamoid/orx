package org.openrndr.extra.fcurve

open class MultiFCurve(val compounds: Map<String, FCurve?>) {
    fun changeSpeed(speed: Double): MultiFCurve {
        return if (speed == 1.0) {
            this
        } else {
            MultiFCurve(compounds.mapValues { it.value?.changeSpeed(speed) })
        }
    }

    /**
     * Duration of the [MultiFCurve]
     */
    val duration by lazy { compounds.values.maxOfOrNull { it?.duration ?: 0.0 } ?: 0.0 }


    /**
     * Start position of the [MultiFCurve]
     */
    val start by lazy { compounds.values.minOfOrNull { it?.start ?: 0.0 } ?: 0.0 }

    /**
     * End position of the [MultiFCurve]
     */
    val end by lazy { compounds.values.maxOfOrNull { it?.end ?: 0.0 } ?: 0.0 }

    operator fun get(name: String): FCurve? {
        return compounds[name]
    }
}

