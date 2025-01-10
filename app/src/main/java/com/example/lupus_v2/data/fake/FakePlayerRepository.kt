package com.example.lupus_v2.data.fake

import com.example.lupus_v2.R
import com.example.lupus_v2.model.PlayerDetails
import com.example.lupus_v2.model.PlayerImageSource
import com.example.lupus_v2.model.manager.PlayerManager
import com.example.lupus_v2.model.roles.Assassino
import com.example.lupus_v2.model.roles.Cittadino
import com.example.lupus_v2.model.roles.Cupido
import com.example.lupus_v2.model.roles.FaciliCostumi
import com.example.lupus_v2.model.roles.Medium
import com.example.lupus_v2.model.roles.Veggente

object FakePlayersRepository {
    private val playerManager = PlayerManager()

    val errorPlayer = PlayerDetails(
        name = "error",
        id = -1,
        role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
        imageSource = PlayerImageSource.DrawableSource(R.drawable.baseline_question_mark_24)
    )
    private var i = 1
    val playerDetails = listOf(
        PlayerDetails(
            name = i++.toString(),
            id = i,
            role = Assassino(),//RoleFactory.createRole(RoleType.Assassino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
        ),
        PlayerDetails(
            name = i++.toString(),
            id = i,
            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero2)
        ),
        PlayerDetails(
            name = i++.toString(),
            id = i,
            role = Veggente(playerManager),//RoleFactory.createRole(RoleType.Veggente),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero3)
        ),
        PlayerDetails(
            name = i++.toString(),
            id = i,
            role = FaciliCostumi(),//RoleFactory.createRole(RoleType.FaciliCostumi),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero4)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Assassino(),//RoleFactory.createRole(RoleType.Assassino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero5)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
        ),

//
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
//        ),





        PlayerDetails(
            name = i++.toString(),id = i,
            role = Assassino(),//RoleFactory.createRole(RoleType.Assassino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero2)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Veggente(playerManager),//RoleFactory.createRole(RoleType.Veggente),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero3)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = FaciliCostumi(),//RoleFactory.createRole(RoleType.FaciliCostumi),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero4)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Assassino(),//RoleFactory.createRole(RoleType.Assassino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero5)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
        ),
        PlayerDetails(
            name = i++.toString(),id = i,
            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
        ),

        PlayerDetails(
            name = "fiorellone gigante",id = ++i,
            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
            imageSource = PlayerImageSource.DrawableSource(R.drawable.cupido_bow)
        ),



//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cupido(),//RoleFactory.createRole(RoleType.Cupido),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero6)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Medium(playerManager),//RoleFactory.createRole(RoleType.Medium),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.ic_launcher_background)
//        ),
//        PlayerDetails(
//            name = i++.toString(),id = i,
//            role = Cittadino(),//RoleFactory.createRole(RoleType.Cittadino),
//            imageSource = PlayerImageSource.DrawableSource(R.drawable.android_superhero1)
//        ),

    )
}