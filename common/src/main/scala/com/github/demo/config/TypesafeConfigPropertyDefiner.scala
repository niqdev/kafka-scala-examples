package com.github.demo
package config

import ch.qos.logback.core.PropertyDefinerBase
import com.typesafe.config.ConfigFactory

import scala.beans.BeanProperty

final class TypesafeConfigPropertyDefiner extends PropertyDefinerBase {
  @BeanProperty var propertyName: String = _

  override def getPropertyValue: String = ConfigFactory.load.getString(propertyName)
}
