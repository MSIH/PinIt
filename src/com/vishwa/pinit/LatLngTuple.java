/*
 * Copyright 2013 Vishwa Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in the 'assets' directory of this 
 * application or at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vishwa.pinit;

import com.google.android.gms.maps.model.LatLng;

public class LatLngTuple {

    private LatLng southwest;
    private LatLng northeast;

    public LatLngTuple(LatLng southwest, LatLng northeast) {
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public LatLng getSouthwest() {
        return southwest;
    }

    public LatLng getNortheast() {
        return northeast;
    }
}
