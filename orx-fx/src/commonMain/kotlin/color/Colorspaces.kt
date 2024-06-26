package org.openrndr.extra.fx.color

import org.openrndr.draw.Filter1to1
import org.openrndr.extra.color.phrases.ColorPhraseBook
import org.openrndr.extra.fx.*
import org.openrndr.extra.shaderphrases.preprocess

class RgbToYCbcr : Filter1to1(mppFilterShader(fx_rgb_to_ycbcr, "rgb-to-ycbcr"))
class YcbcrToRgb : Filter1to1(mppFilterShader(fx_ycbcr_to_rgb, "ycbcr_to_rgb"))

class RgbToOkLab : Filter1to1(mppFilterShader(run {
    ColorPhraseBook.register()
    fx_rgb_to_oklab.preprocess()
}, "rgb-to-oklab"))

class OkLabToRgb : Filter1to1(mppFilterShader(run {
    ColorPhraseBook.register()
    fx_oklab_to_rgb.preprocess()
}, "oklab-to-rgb"))
