package com.example.lupus_v2.model.roles

import com.example.lupus_v2.model.manager.PlayerManager

enum class RoleType{
    Assassino,
    Cittadino,
    Cupido,
    Seduttrice,
    Medium,
    Veggente,
    //A,B,C,D,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z
}

class RoleFactory(
    private val playerManager: PlayerManager
){
    private val rolesMap = mutableMapOf<RoleType, Role>()

    fun createRole(roleType: RoleType): Role {
        return rolesMap.getOrPut(roleType) {
            when (roleType) {
                RoleType.Assassino -> Assassino()
                RoleType.Cittadino -> Cittadino()
                RoleType.Cupido -> Cupido()
                RoleType.Seduttrice -> FaciliCostumi()
                RoleType.Medium -> Medium(playerManager)
                RoleType.Veggente -> Veggente(playerManager)
                else -> Assassino()
            }
        }
    }

}