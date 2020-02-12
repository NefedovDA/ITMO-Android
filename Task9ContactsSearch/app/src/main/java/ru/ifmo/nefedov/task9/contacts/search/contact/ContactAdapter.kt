package ru.ifmo.nefedov.task9.contacts.search.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.TestOnly
import ru.ifmo.nefedov.task9.contacts.search.R

class ContactAdapter(
    private val onClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactViewHolder>() {
    var contacts: List<Contact> = listOf()
        @TestOnly get
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
        holder.root.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }
        return holder
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.contactNameText.text = contacts[position].name
        holder.contactPhoneText.text = contacts[position].phone
    }
}
