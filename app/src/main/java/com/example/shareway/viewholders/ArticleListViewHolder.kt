package com.example.shareway.viewholders

import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shareway.R
import com.example.shareway.databinding.ArticleListItemBinding
import com.example.shareway.listeners.OnArticleClickListener
import com.example.shareway.listeners.ReminderListeners
import com.example.shareway.models.Article
import com.example.shareway.utils.managers.DateTimeManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ArticleListViewHolder(
    private val binding: ArticleListItemBinding,
    private val onArticleClickListener: OnArticleClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

    companion object {
        private const val TAG = "ArticleListViewHolder"
    }

    init {
        binding.root.setOnClickListener(this)
        binding.setDateButton.setOnClickListener(this)
//        itemView.setOnClickListener(object : DoubleClickListener() {
//            override fun onDoubleClick(v: View) {
//                Log.d(TAG, "onDoubleClick: DOUBLE CLICKED")
//            }
//
//        })
        binding.root.setOnLongClickListener(this)
        binding.popupMenu.setOnClickListener(this)
        binding.reminderIcon.setOnClickListener(this)

    }



    fun bind(articleItem: Article?) {

        Log.d(TAG, "bind: $articleItem ")
        articleItem?.let {
            Log.d(TAG, "bind: Date Added: ${it.dateAdded}")
            binding.apply {

                reminderIcon.visibility = if (it.reminder != null) View.VISIBLE else View.GONE

                if (it.alreadyRead) alreadyReadIcon1.visibility =
                    View.VISIBLE else alreadyReadIcon1.visibility = View.GONE
                titleText.text = articleItem.title

                Glide.with(itemView)
                    .load(articleItem.articleImage)
                    .centerCrop()
                    .error(Glide.with(itemView).load(articleItem.defaultImage))
                    .into(articleImage)


                val dateFormat = LocalDateTime.ofInstant(it.dateAdded, ZoneOffset.UTC)
//                date.text = dateFormat.toString()
//                date.text = dateFormat.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
                date.text = dateFormat.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

                Log.d(TAG, "bind: ALREADY READ ${it.alreadyRead}")
                if (it.alreadyRead) binding.alreadyReadIcon.visibility =
                    View.VISIBLE else binding.alreadyReadIcon.visibility = View.GONE

                reminderText.text = it.reminder?.toString() ?: "No reminder for this article"
            }
        }

    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: clicked")
//        if (v == binding.setDateButton) onArticleClickListener.onSetRemainderButtonClick(
//            adapterPosition
////        ) else onArticleClickListener.onArticleClick(adapterPosition)
        when (v) {
            binding.setDateButton -> onSetReminderClicked()
            binding.popupMenu -> showPopupMenu(v)
            binding.reminderIcon -> showReminderDialog(binding.reminderText.text.toString())
            else -> onArticleClickListener.onArticleClick(adapterPosition)
        }

    }

    private fun showReminderDialog(text: String) {
        onArticleClickListener.onReminderIconClick(text, adapterPosition)
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(v.context, v)
        popupMenu.inflate(R.menu.article_list_item_menu)
        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.mark_as_read -> {
                    Log.d(TAG, "showPopupMenu: mark as read clicked")
                    Log.d(TAG, "onLongClick: clicked")
                    //check if true or false
                    if (binding.alreadyReadIcon1.isVisible) {
                        Log.d(TAG, "bind: ALREADY READ VISIBILITY = VISIBLE! NOW IT ISN'T")
                        binding.alreadyReadIcon1.visibility = View.GONE
                    } else {
                        Log.d(TAG, "bind: ALREADY READ VISIBILITY = NOT(!!!) VISIBLE! NOW IT IS")
                        binding.alreadyReadIcon1.visibility = View.VISIBLE
                    }
                    //true if the callback consumed the long click, false otherwise.
                    onArticleClickListener.onLongArticleClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.set_reminder -> {
                    Log.d(TAG, "showPopupMenu: set reminder clicked")
                    onSetReminderClicked()
                    return@setOnMenuItemClickListener true
                }

                R.id.add_note -> {
                    onArticleClickListener.onAddNoteClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                R.id.view_notes -> {
                    onArticleClickListener.onViewNotesClick(adapterPosition)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener true

            }
        }
        popupMenu.show()
    }


    /**
     *
     * onLongClick - check visibility and set the opposite.
     * Insert isAlreadyRead status to db
     *
     * TODO: REMOVE, LEGACY CODE BEFORE THE ACTIONM ODE
     * **/
    override fun onLongClick(v: View?): Boolean {
        Log.d(TAG, "onLongClick: clicked")
        //check if true or false
        if (binding.alreadyReadIcon.isVisible) {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = VISIBLE! NOW IT ISN'T")
            binding.alreadyReadIcon.visibility = View.GONE
        } else {
            Log.d(TAG, "bind: ALREADY READ VISIBILITY = NOT(!!!) VISIBLE! NOW IT IS")
            binding.alreadyReadIcon.visibility = View.VISIBLE
        }
        //true if the callback consumed the long click, false otherwise.
        onArticleClickListener.onLongArticleClick(adapterPosition)
        return true
    }

    fun triggerOnClickListener() {
        onClick(itemView)
    }


    private fun onSetReminderClicked() {
        DateTimeManager.launchDateTimeDialogs(binding.root.context,
            object : ReminderListeners {
                override fun onSuccess(reminder: Instant, hour: Int, minute: Int, day:Int) {
                    onArticleClickListener.onReminderSet(
                        adapterPosition,
                        reminder,
                        hour,
                        minute,
                        day
                    )

                    /**
                     *
                     * TODO: Cancel when reminder end. implement funcion what happen when reminder done(mayble check on reycler launch if there is active reminder will be a better solution)
                     *
                     * **/
                    binding.reminderIcon.visibility = View.VISIBLE
//            val string = "Date: ${headerText} Time: ${timePicker.hour}:${timePicker.minute}"
//            binding.reminderText.text = string
                }

            }
        )
//        val datePicker = getDatePickerDialog()
//        Log.d(TAG, "onSetReminderClicked: $datePicker")
//
//        datePicker.show(
//            (binding.root.context as AppCompatActivity).supportFragmentManager,
//            datePicker.toString()
//        )
//
//        handleDatePickerOnPositiveButtonClicked(datePicker)
//        handleDatePickerOnCancelOrNegativeButtonClicked(datePicker)
    }

    /**
     *
     *
     * DatePicker Handlers
     *
     * **/

//
//    private fun handleDatePickerOnCancelOrNegativeButtonClicked(datePicker: MaterialDatePicker<Long>) {
//        datePicker.addOnCancelListener {
////            uiCommunicationListener.onResponseReceived("cancel", UIComponentType.Toast)
//
//        }
//        datePicker.addOnNegativeButtonClickListener {
////            uiCommunicationListener.onResponseReceived("negative", UIComponentType.Toast)
//
//        }
//    }
//
//    private fun handleDatePickerOnPositiveButtonClicked(datePicker: MaterialDatePicker<Long>) {
//        /**
//         *
//         * The picker interprets all long values as milliseconds from the UTC Epoch.
//         * We can use Instant.ofEpochMilli(dateSelected) and LocalDateTime.ofInstant(...) otherwise Calendar.setTimeInMillis(dateSelected).
//         * Attention to LocalDateTime.ofEpochSecond; it works with seconds and not milliseconds
//         *
//         * **/
//
//
//        datePicker.addOnPositiveButtonClickListener {
//            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: ")
//            val timePicker = getTimePickerDialog()
//            timePicker.show(
//                (binding.root.context as AppCompatActivity).supportFragmentManager,
//                "tag"
//            )
//
//            Log.d(
//                TAG,
//                "handleDatePickerOnPositiveButtonClicked: SELECTION: ${datePicker.selection}"
//            )
//            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: SELECTION: $it")
//            Log.d(
//                TAG,
//                "handleDatePickerOnPositiveButtonClicked: SELECTION: ${Instant.ofEpochMilli(it)}"
//            )
//            Log.d(TAG, "handleDatePickerOnPositiveButtonClicked: SELECTION: ")
//
//
//
//            handleTimePickerOnPositiveButtonClicked(timePicker, datePicker, datePicker.headerText)
//            handleTimePickerOnCancelOrNegativeButtonClicked(timePicker)
//
//
//            //TODO: Handle time dismiss. set the reminder time to the current time.
//            //TODO: show toast with the time + date
//
//        }
//    }
//
//    private fun handleTimePickerOnCancelOrNegativeButtonClicked(timePicker: MaterialTimePicker) {
//
//    }
//
//    private fun handleTimePickerOnPositiveButtonClicked(
//        timePicker: MaterialTimePicker,
//        datePicker: MaterialDatePicker<Long>,
//        headerText: String
//
//    ) {
//        timePicker.addOnPositiveButtonClickListener {
//            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: $it")
//            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${timePicker.inputMode}")
//            Log.d(
//                TAG,
//                "handleTimePickerOnPositiveButtonClicked: ${timePicker.minute.javaClass.name}"
//            )
//            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${timePicker.hour}")
//
//            Log.d(TAG, "handleTimePickerOnPositiveButtonClicked: ${datePicker.selection}")
//
//            val reminder = Instant.ofEpochMilli(datePicker.selection!!)
//                .plus(timePicker.hour.toLong(), ChronoUnit.HOURS)
//                .plus(timePicker.minute.toLong(), ChronoUnit.MINUTES)
//
//            Log.d(
//                TAG,
//                "handleTimePickerOnPositiveButtonClicked: inst with time $reminder epoch with time ${reminder.toEpochMilli()}"
//            )
//
//
//
//            Log.d(
//                TAG,
//                "handleTimePickerOnPositiveButtonClicked: ${headerText} ${timePicker.hour} ${timePicker.minute}"
//            )
//
//            onArticleClickListener.onReminderSet(
//                adapterPosition,
//                reminder,
//                timePicker.hour,
//                timePicker.minute
//            )
//
//            /**
//             *
//             * TODO: Cancel when reminder end. implement funcion what happen when reminder done(mayble check on reycler launch if there is active reminder will be a better solution)
//             *
//             * **/
//            binding.reminderIcon.visibility = View.VISIBLE
////            val string = "Date: ${headerText} Time: ${timePicker.hour}:${timePicker.minute}"
////            binding.reminderText.text = string
//
//        }
//    }
//
//
//    /**
//     *
//     * Get DatePicker & TimePicker Dialogs
//     *
//     * **/
//
//    private fun getTimePickerDialog(): MaterialTimePicker {
//        val calendar = Calendar.getInstance(Locale.getDefault())
//        return MaterialTimePicker.Builder()
//            .setTimeFormat(TimeFormat.CLOCK_24H)
//            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
//            .setMinute(calendar.get(Calendar.MINUTE))
//            .build()
//    }
//
//    private fun getDatePickerDialog(): MaterialDatePicker<Long> {
//        val calendar = Calendar.getInstance(Locale.getDefault())
//
//        /**
//         *
//         *
//         *  Depending on the type of MaterialDatePicker you have built, we can handle this in one of two ways.
//         *  For a DatePicker, the selected value would be passed a unix epoch time value in a Long variable that is accessible as an argument in the PositiveButtonClickedListener lambda.
//         *  it can also be read as a string in the header text of the Picker( Jan 10, Jul 28 etc)
//         *
//         *
//         * **/
//        val buildDatePicker = MaterialDatePicker.Builder.datePicker()
//        buildDatePicker.setTitleText("Select Reminder Date")
//        buildDatePicker.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//        return buildDatePicker.build()
//    }


}