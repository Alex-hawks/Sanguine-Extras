package io.github.alex_hawks.SanguineExtras.common.util.config

/**
  * @param enabledFilters all the filters that this mob can respond to, defaults to all of them. If empty, the mob cannot be pushed
  * @param entityID       the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance
  * @param generated      if false, do not override the other values on change to the defaults. never override this
  */
case class InterdictionEntry(enabledFilters: Array[String], entityID: String, generated: Boolean = true)

/**
  * @param enabled      if false, this mob cannot be captured
  * @param cost         see Base.Sigil.Holding.cost
  * @param maxHealth    see Base.Sigil.Holding.maxHealth
  * @param isPercentage see Base.Sigil.Holding.maxHealthIsPercentage
  * @param entityID the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance
  * @param generated    if false, do not override the other values on change to the defaults. never override this
  */
case class CaptureEntry(enabled: Boolean, cost: Int, maxHealth: Double, isPercentage: Boolean, entityID: String, generated: Boolean = true) {
  def multiplyCost(mul: Double): CaptureEntry = *(mul)
  def *(mul: Double): CaptureEntry = this.copy(cost = (this.cost * mul).round.intValue)
  def withEntityID(str: String): CaptureEntry = this.copy(entityID = str)
}

/**
  * @param enabled      if false, this mob cannot be spawned
  * @param lpMultiplier see Base.Ritual.Spawn.lpMultiplier
  * @param maxEntities  see Base.Ritual.Spawn.maxEntities. This is before the /10 is done for bosses
  * @param entityID     the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance
  * @param generated    if false, do not override the other values on change to the defaults. never override this
  */
case class SpawnEntry(enabled: Boolean, lpMultiplier: Int, maxEntities: Int, entityID: String, generated: Boolean = true) {
  def multiplyCost(mul: Double): SpawnEntry = *(mul)
  def *(mul: Double): SpawnEntry = this.copy(lpMultiplier = (this.lpMultiplier * mul).round.intValue)
  def withEntityID(str: String): SpawnEntry = this.copy(entityID = str)
}
