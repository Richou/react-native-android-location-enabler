package com.heanoria.library.reactnative.locationenabler

class AndroidLocationEnablerException : Exception {
  constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable)
  constructor(detailMessage: String?) : super(detailMessage)
}
