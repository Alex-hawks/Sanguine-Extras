package io.github.alex_hawks.SanguineExtras.common.rituals.basic

import WayofTime.bloodmagic.ritual.Ritual

trait BaseRitual extends Ritual {

  def getNewCopy: Ritual = this.getClass.newInstance
}
