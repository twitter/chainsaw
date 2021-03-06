package com.twitter.chainsaw

import org.slf4j

object Logger {
  /**
   * Return a logger for the class name of the class/object that called
   * this method. Normally you would use this in a "private val"
   * declaration on the class/object. The class name is determined
   * by sniffing around on the stack.
   */
  def apply(): Logger =  {
    val stack = new Throwable().getStackTrace()
    val name = stack.view.map(_.getClassName).map(cleanupClassName)
      .find(!_.startsWith("com.twitter.chainsaw.Log")).get
    apply(name)
  }

  /**
   * Returns a logger for the name of the specified class.
   */
  def apply(clz: Class[_]): Logger = {
    new Logger(slf4j.LoggerFactory.getLogger(clz))
  }

  /**
   * Returns a logger for the specified name
   */
  def apply(name: String): Logger = {
    new Logger(slf4j.LoggerFactory.getLogger(name))
  }

  /**
   * Returns a logger for the class of the specified object
   */
  def apply(obj: AnyRef): Logger = apply(obj.getClass())

  private def cleanupClassName(className: String) = {
    if (className.endsWith("$"))
      className.substring(0, className.length - 1)
    else if (className.endsWith("$class"))
      className.substring(0, className.length - 6)
    else
      className
  }
}

class Logger(val underlying: slf4j.Logger) {
  import String.format

  def isTraceEnabled = underlying.isTraceEnabled
  def isDebugEnabled = underlying.isDebugEnabled
  def isInfoEnabled = underlying.isInfoEnabled
  def isWarnEnabled = underlying.isWarnEnabled
  def isErrorEnabled = underlying.isErrorEnabled

  def trace(msg: String) = underlying.trace(msg)
  def trace(msg: String, items: Any*) =
    if (isTraceEnabled) underlying.trace(msg.format(items: _*))
  def trace(thrown: Throwable, msg: String, items: Any*) =
    if (isTraceEnabled) underlying.trace(msg.format(items: _*), thrown)

  def debug(msg: String) = underlying.debug(msg)
  def debug(msg: String, items: Any*) =
    if (isDebugEnabled) underlying.debug(msg.format(items: _*))
  def debug(thrown: Throwable, msg: String, items: Any*) =
    if (isDebugEnabled) underlying.debug(msg.format(items: _*), thrown)

  def info(msg: String) = underlying.info(msg)
  def info(msg: String, items: Any*) =
    if (isInfoEnabled) underlying.info(msg.format(items: _*))
  def info(thrown: Throwable, msg: String, items: Any*) =
    if (isInfoEnabled) underlying.info(msg.format(items: _*), thrown)

  def warn(msg: String) = underlying.warn(msg)
  def warn(msg: String, items: Any*) =
    if (isWarnEnabled) underlying.warn(msg.format(items: _*))
  def warn(thrown: Throwable, msg: String, items: Any*) =
    if (isWarnEnabled) underlying.warn(msg.format(items: _*), thrown)

  def error(msg: String) = underlying.error(msg)
  def error(msg: String, items: Any*) =
    if (isErrorEnabled) underlying.error(msg.format(items: _*))
  def error(thrown: Throwable, msg: String, items: Any*) =
    if (isErrorEnabled) underlying.error(msg.format(items: _*), thrown)

  def ifTrace(msg: => String) = if (isTraceEnabled) underlying.trace(msg)
  def ifTrace(thrown: Throwable, msg: => String) = if (isTraceEnabled) underlying.trace(msg, thrown)

  def ifDebug(msg: => String) = if (isDebugEnabled) underlying.debug(msg)
  def ifDebug(thrown: Throwable, msg: => String) = if (isDebugEnabled) underlying.debug(msg, thrown)

  def ifInfo(msg: => String) = if (isInfoEnabled) underlying.info(msg)
  def ifInfo(thrown: Throwable, msg: => String) = if (isInfoEnabled) underlying.info(msg, thrown)

  def ifWarn(msg: => String) = if (isWarnEnabled) underlying.warn(msg)
  def ifWarn(thrown: Throwable, msg: => String) = if (isWarnEnabled) underlying.warn(msg, thrown)

  def ifError(msg: => String) = if (isErrorEnabled) underlying.error(msg)
  def ifError(thrown: Throwable, msg: => String) = if (isErrorEnabled) underlying.error(msg, thrown)
}
