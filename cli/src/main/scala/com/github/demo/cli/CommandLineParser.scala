package com.github.demo
package cli

import com.github.demo.cli.Game.Game
import com.github.demo.config.CommonSettings

/**
  * Type of game
  */
object Game extends Enumeration {
  type Game = Value
  val Rock, Paper, Scissors, Invalid = Value
}

/**
  * Command line parameters
  *
  * @param player player
  * @param game   game [[Game]]
  * @param age    age
  */
final case class Config(player: String = "", game: Game = Game.Invalid, age: Int = -1)

/**
  * Application parameters
  *
  * @param player player
  * @param game   game [[Game]]
  * @param age    age
  */
final case class Params(player: String, game: Game, age: Option[Int])

/**
  * Command line parser
  */
object CommandLineParser {
  private[this] lazy val commonSettings = CommonSettings

  private[this] implicit val entityRead: scopt.Read[Game.Value] =
    scopt.Read.reads(Game withName _)

  private[this] val parser = new scopt.OptionParser[Config]("niqdev") {
    head(commonSettings.name)

    opt[String]('p', "player")
      .required()
      .action((x, c) => c.copy(player = x))
      .text("player required")

    opt[Game]('g', "game")
      .required()
      .action((x, c) => c.copy(game = x))
      .text("game required")

    opt[Int]('a', "age")
      .action((x, c) => c.copy(age = x))
      .text("optional age")
  }

  protected[cli] def parseAge(age: Int): Option[Int] = age match {
    case value if value < 0 => None
    case _                  => Some(age)
  }

  /**
    * Parse parameters
    *
    * @param args input parameters
    * @return validated parameters
    */
  def parse(args: Seq[String]): Either[String, Params] =
    parser.parse(args, Config()) match {
      case Some(config) =>
        Right(Params(config.player, config.game, parseAge(config.age)))
      case None =>
        Left(s"invalid command line arguments")
    }

}
