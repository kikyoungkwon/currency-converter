package com.kikyoung.currency.feature.list

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kikyoung.currency.feature.list.model.CurrencyRate
import com.kikyoung.currency.util.CurrencyUtil
import kotlinx.android.synthetic.main.item_currency_rate.view.*
import java.util.concurrent.locks.ReentrantLock

class CurrencyListAdapter : RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {

    companion object {
        @VisibleForTesting
        const val DEFAULT_BASE_CURRENCY_RATE = 100.0
    }

    private val currencyRates = mutableListOf<CurrencyRate>()
    private var baseCurrencyRate = Double.MIN_VALUE
    private var itemClickListener: OnItemClickListener? = null

    private val lock = ReentrantLock()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                com.kikyoung.currency.R.layout.item_currency_rate,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = currencyRates.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(position, currencyRates[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int, currencyRate: CurrencyRate) {
            view.apply {
                val isFirstRow = position == 0
                setFlagImageView(currencyRate)
                codeTextView.text = currencyRate.code
                nameTextView.text = currencyRate.displayName
                setRateEditText(isFirstRow, currencyRate)
                if (isFirstRow) setOnClickListener(null)
                else setOnClickListener {
                    moveItem(position)
                    itemClickListener?.onItemClick(currencyRate.code)
                }
            }
        }

        private fun setFlagImageView(currencyRate: CurrencyRate) =
            view.apply {
                Glide
                    .with(this)
                    .load(currencyRate.flagUrl)
                    .centerCrop()
                    .into(flagImageView)
            }

        private fun setRateEditText(isFirstRow: Boolean, currencyRate: CurrencyRate) =
            view.apply {
                rateEditText.apply {
                    isEnabled = isFirstRow
                    setText(CurrencyUtil.format(currencyRate.code, currencyRate.rate * baseCurrencyRate))
                    if (isFirstRow) {
                        setSelection(text.length)
                        addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (isEnabled && tag?.toString() != s.toString())
                                    post {
                                        val string = s.toString()
                                        baseCurrencyRate = if (string.isEmpty()) 0.0 else string.toDouble()
                                        tag = s
                                        // Can not call directly within bind() which is called from notifyDataSetChanged(),
                                        // so use post().
                                        notifyDataSetChanged()
                                    }
                            }
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        })
                    }
                }
            }
    }

    private fun moveItem(position: Int) {
        lock.lock()
        try {
            // Make the selected currency rate to 1 and change others accordingly
            val selectedCurrencyRate = currencyRates[position]
            val rates = currencyRates.map { currencyRate ->
                CurrencyRate(
                    currencyRate.code,
                    currencyRate.displayName,
                    currencyRate.rate / selectedCurrencyRate.rate,
                    currencyRate.flagUrl
                )
            }
            currencyRates.clear()
            currencyRates += rates as Collection<CurrencyRate>
            // Switch selected currency with previously selected currency at the top
            currencyRates.removeAt(position).also {
                currencyRates.add(0, it)
            }
            baseCurrencyRate *= selectedCurrencyRate.rate

            // Do item moving animation
            notifyItemMoved(position, 0)
            notifyItemRangeChanged(0, position + 1)
        } finally {
            lock.unlock()
        }
    }

    fun updateCurrencyRates(newCurrencyRates: List<CurrencyRate>) {
        lock.lock()
        try {
            if (currencyRates.isEmpty()) {
                baseCurrencyRate = DEFAULT_BASE_CURRENCY_RATE
                currencyRates += newCurrencyRates

            } else {
                // To keep the order of list, update each item
                val rates = currencyRates.map { currencyRate ->
                    newCurrencyRates.find { currencyRate.code == it.code }
                }
                currencyRates.clear()
                currencyRates += rates as Collection<CurrencyRate>
                notifyDataSetChanged()
            }
            notifyDataSetChanged()
        } finally {
            lock.unlock()
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(currencyCode: String)
    }
}