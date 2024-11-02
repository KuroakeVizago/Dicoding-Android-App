package com.kuroakevizago.dicodingapp.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kuroakevizago.dicodingapp.databinding.FragmentSettingsBinding
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.preferences.SettingsPreferences
import com.kuroakevizago.dicodingapp.preferences.dataStore
import com.kuroakevizago.dicodingapp.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit


class SettingsFragment : Fragment() {

    private lateinit var _settingsBinding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Model Setup
        val pref = SettingsPreferences.getInstance(requireActivity().application.dataStore)
        val viewModelFactory = ViewModelFactory.getInstance(pref)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _settingsBinding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        createNotificationChannel(requireContext())
        themeSetup()
        notificationSetup()

        return _settingsBinding.root
    }

    private fun themeSetup() {
        val switchTheme = _settingsBinding.switchTheme
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Daily Reminder Channel"
            val descriptionText = "Channel to notify nearest upcoming event"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("dailyReminderChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificationSetup() {
        val switchDailyReminder = _settingsBinding.switchDailyReminder
        viewModel.getNotificationSetting().observe(viewLifecycleOwner) {isNotificationActive: Boolean ->
            if (switchDailyReminder.isChecked != isNotificationActive)
                switchDailyReminder.isChecked = isNotificationActive

            switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveNotificationSetting(isChecked)
                if (isChecked) {
                    startDailyReminder()
                } else {
                    stopDailyReminder()
                }
            }
        }


    }

    private fun startDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        context?.let {
            WorkManager.getInstance(it).enqueue(dailyWorkRequest)
        }
    }

    private fun stopDailyReminder() {
        context?.let {
            WorkManager.getInstance(it).cancelAllWork()
        }
    }
}