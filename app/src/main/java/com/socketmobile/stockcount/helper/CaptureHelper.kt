/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.helper
import com.socketmobile.capture.client.*
import com.socketmobile.capture.types.*

fun DeviceClient.isSocketCamDevice(): Boolean {
    return this.deviceType == DeviceType.kModelSocketCamC820
}
