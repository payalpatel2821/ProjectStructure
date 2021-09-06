package com.task.newapp.adapter.contact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.models.contact.Contact
import com.task.newapp.models.contact.ContactRecyclerViewModel
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog
import de.hdodenhof.circleimageview.CircleImageView


class ContactAdapter(
    private val mainActivity: FragmentActivity

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer {

    val TYPE_COUNTRY = 0
    val TYPE_LETTER = 1
    private var allContact: ArrayList<ContactRecyclerViewModel> = ArrayList()
    var onItemClick: ((String, String) -> Unit)? = null
    private var totalSize = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COUNTRY -> {
                val view: View = LayoutInflater.from(mainActivity).inflate(
                    R.layout.item_explore_contact_number,
                    parent,
                    false
                )
                return ContactViewHolder(view)
            }
            TYPE_LETTER -> {
                val view: View = LayoutInflater.from(mainActivity).inflate(
                    R.layout.row_recyclerview_country,
                    parent,
                    false
                )
                return LetterViewHolder(view)
            }
            else -> {
                val view: View = LayoutInflater.from(mainActivity).inflate(
                    R.layout.item_explore_contact_number,
                    parent,
                    false
                )
                return ContactViewHolder(view)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val contactRecyclerViewModel: ContactRecyclerViewModel = allContact[position]
        when (getItemViewType(position)) {
            TYPE_COUNTRY -> {
                val contact: Contact = contactRecyclerViewModel.contact!!
                if (contact != null) {
                    (holder as ContactViewHolder).bindTo(
                        holder,
                        contactRecyclerViewModel.contact!!,
                        mainActivity,
                        onItemClick,
                        position,
                        allContact.size
                    )
                }
            }
            TYPE_LETTER -> {
                (holder as LetterViewHolder).bindTo(contactRecyclerViewModel.letter)
            }
        }

    }

    override fun getItemCount(): Int {
        return if (allContact.isNotEmpty()) allContact.size else 0
    }

    override fun getSections(): Array<Any> {
        val sectionList: MutableList<String> = ArrayList()

        for (country in allContact) {
            if (country.type == TYPE_LETTER) {
                sectionList.add(country.letter)
            }
        }

        return sectionList.toTypedArray()
    }

    override fun getPositionForSection(sectionIndex: Int): Int {

        var i = 0
        val size: Int = allContact.size
        while (i < size) {
            val contactRecyclerViewModel: ContactRecyclerViewModel = allContact[i]
            if (contactRecyclerViewModel.type == TYPE_LETTER) {
                val sortStr = contactRecyclerViewModel.letter
                val firstChar = sortStr.toUpperCase()[0]
                if (firstChar == sectionIndex.toChar()) {
                    return i
                }
            }
            i++
        }


        return -1
    }

    override fun getSectionForPosition(position: Int): Int {
        var positionMax = position

        val realSize = itemCount
        if (position >= realSize) {
            positionMax = realSize - 1
        }

        val contactRecyclerViewModel: ContactRecyclerViewModel = allContact[positionMax]
        val sectionArray = sections

        var letter = ""
        when (contactRecyclerViewModel.type) {
            TYPE_COUNTRY -> {
                val country: Contact = contactRecyclerViewModel.contact!!
                if (country != null) {
                    letter = country.firstName.substring(0, 1)
                }
            }
            TYPE_LETTER -> {
                letter = contactRecyclerViewModel.letter
            }
        }

        for (i in sectionArray.indices) {
            if (sectionArray[i].toString() == letter) {
                return i
            }
        }
        return -1
    }

    override fun getItemViewType(position: Int): Int {
        val contact: ContactRecyclerViewModel = allContact[position]
        return contact.type ?: super.getItemViewType(position)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var mobileNumber: String
        private lateinit var emailId: String
        private val imageView: CircleImageView = itemView.findViewById(R.id.item_contact_image)
        private val textViewName: TextView = itemView.findViewById(R.id.item_contact_name)
        private val textViewNumber: TextView = itemView.findViewById(R.id.item_contact_number)
        private val divider: View = itemView.findViewById(R.id.divider)

        fun bindTo(holder: ContactViewHolder, contact: Contact, context: Context, onItemClick: ((String, String) -> Unit)?, position: Int, totalSize: Int) {
            textViewName.text = contact.getNameToDisplay()
            if (contact.phoneNumbers.isNotEmpty()) {
                textViewNumber.text = contact.phoneNumbers[0].normalizedNumber
                mobileNumber = contact.phoneNumbers.joinToString(separator = ",") { it.normalizedNumber!!.replace("+91", "") ?: "" }
                textViewNumber.visibility = VISIBLE
            } else {
                mobileNumber = ""
                textViewNumber.visibility = GONE
            }
            emailId = if (contact.emails.isNotEmpty()) {
                contact.emails.joinToString(separator = ", ") { it.label ?: "" }
            } else {
                ""
            }
            imageView.load(contact.photoUri, true, textViewName.text.toString().trim(), "#6CAEC4")

            showLog("size===", "$position $totalSize")
            if (position == totalSize-1) {
                holder.divider.visibility = GONE
            } else {
                holder.divider.visibility = VISIBLE
            }

            holder.itemView.setOnClickListener {
                if (onItemClick != null) {
                    onItemClick!!.invoke(mobileNumber, emailId)
                }
            }
        }
    }

    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewLetter: TextView =
            itemView.findViewById<View>(R.id.textview_country) as TextView

        fun bindTo(letter: String) {
            textViewLetter.text = letter
        }

    }

    fun filterList(filteredList: ArrayList<ContactRecyclerViewModel>) {
        allContact = filteredList
        notifyDataSetChanged()
    }


}