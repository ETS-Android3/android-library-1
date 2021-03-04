/* Copyright Airship and Contributors */

package com.urbanairship.debug.automation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.urbanairship.automation.Schedule
import com.urbanairship.automation.ScheduleData
import com.urbanairship.debug.databinding.UaItemAutomationBinding

internal class ScheduleListAdapter(private val callback: ((schedule: Schedule<out ScheduleData>) -> Unit)) : ListAdapter<Schedule<out ScheduleData>, ScheduleListAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(val binding: UaItemAutomationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply {
            with(holder.binding) {
                viewModel = ScheduleListItem(this@apply)
                root.setOnClickListener {
                    this@ScheduleListAdapter.callback(this@apply)
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UaItemAutomationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Schedule<out ScheduleData>>() {
            override fun areItemsTheSame(oldItem: Schedule<out ScheduleData>, newItem: Schedule<out ScheduleData>): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Schedule<out ScheduleData>, newItem: Schedule<out ScheduleData>): Boolean {
                return oldItem == newItem
            }
        }
    }
}
