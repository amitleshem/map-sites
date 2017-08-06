package com.wixpress.mapsites

import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Properties.GeoCoordinates


case class Address(street: String, city: String, country: Country, coordinates: Option[GeoCoordinates])

