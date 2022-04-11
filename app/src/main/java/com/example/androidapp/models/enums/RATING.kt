package com.example.androidapp.models.enums

import com.example.androidapp.R

enum class RATING(val iconId: Int, val colorId: Int) {
    VERY_BAD(R.drawable.ic_face_angry_solid, R.color.very_bad),
    BAD(R.drawable.ic_face_frown_solid, R.color.bad),
    OK(R.drawable.ic_face_meh_solid, R.color.ok),
    GOOD(R.drawable.ic_face_smile_solid, R.color.good),
    VERY_GOOD(R.drawable.ic_face_laugh_beam_solid, R.color.very_good)
}