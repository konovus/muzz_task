package com.example.muzz_task.util

import com.airbnb.epoxy.EpoxyDataBindingLayouts
import com.airbnb.epoxy.PackageModelViewConfig
import com.example.muzz_task.R

@PackageModelViewConfig(rClass = R::class)
@EpoxyDataBindingLayouts(value = [R.layout.sender_message_item, R.layout.timestamp_item, R.layout.replayer_message_item,
                                ])
interface EpoxyConfig