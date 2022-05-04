package androidapp.CO2Mad.views.adapters

import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidapp.CO2Mad.R
import android.widget.Toast
import androidapp.CO2Mad.models.Country
import androidapp.CO2Mad.models.Product
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.tools.enums.Completed
import androidapp.CO2Mad.viewmodels.ScannerViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.card_layout_alt.view.*


class ScannerAdapter(
    var purchases: List<Purchase>,
    val products: List<Product>,
    val countries: List<Country>,
    private val viewModel: ScannerViewModel,
    val resources: Resources,
    val currentList: Completed
) : RecyclerView.Adapter<ScannerAdapter.ViewHolder>() {
    val defaultTextColor = resources.getColor(R.color.defaultValue)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_alt, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (purchases.size > 0) {
            val purchase = purchases[position]

            setUpTitle(holder, purchase)
            setUpToggleButton(holder, purchase)
            setUpProductDropdown(holder, purchase)
            setUpCountryDropdown(holder, purchase)
            setUpAmountField(holder, purchase)
            setUpWeightField(holder, purchase)
            setupButtons(holder, purchase)

        }

    }

    private fun setupButtons(holder: ViewHolder, purchase: Purchase) {
        val toast1 = Toast.makeText(
            holder.itemView.context,
            "Varen er blevet fjernet fra listen.",
            Toast.LENGTH_SHORT
        )
        val toast2 = Toast.makeText(
            holder.itemView.context,
            "Varen er flyttet til \"Udfyldte\".",
            Toast.LENGTH_SHORT
        )
        val toast3 = Toast.makeText(
            holder.itemView.context,
            "Kan ikke flyttes til \"Udfyldte\".",
            Toast.LENGTH_SHORT
        )

        if (currentList == Completed.COMPLETED) {
            holder.acceptButton.visibility = Button.GONE
        }
        holder.deleteButton.setOnClickListener {
            toast1.setGravity(Gravity.TOP, 0, 0)
            toast1.show()
            viewModel.onDeletePurchase(holder.adapterPosition, currentList)
        }
        holder.acceptButton.setOnClickListener {
            if(purchase.isValid()) {
                toast2.setGravity(Gravity.TOP, 0, 0)
                toast2.show()
                viewModel.onCompletedChange(holder.adapterPosition)
            } else {
                toast3.setGravity(Gravity.TOP, 0, 0)
                toast3.show()
            }

        }

    }

    private fun setUpTitle(holder: ViewHolder, purchase: Purchase) {
        holder.title.doAfterTextChanged {
            if (holder.title.text!!.isEmpty()) {
                holder.title.error = "Indtast tekst"
            } else {
                viewModel.onReceiptTextChanged(holder.adapterPosition, it.toString(), currentList)
            }
        }

        holder.title.setText(purchase.storeItem.receiptText.toUpperCase())
    }

    private fun setUpToggleButton(holder: ViewHolder, purchase: Purchase) {
        if (purchase.storeItem.organic) {
            holder.toggleButton.check(R.id.btn_organic)
        }

        if (!purchase.storeItem.packaged) {
            holder.toggleButton.check(R.id.btn_packaged)
        }

        holder.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.btn_packaged -> viewModel.onPackagedChanged(
                    holder.adapterPosition,
                    !isChecked,
                    currentList
                )
                R.id.btn_organic -> viewModel.onOrganicChanged(
                    holder.adapterPosition,
                    isChecked,
                    currentList
                )

            }
        }
    }

    private fun setUpProductDropdown(holder: ViewHolder, purchase: Purchase) {
        holder.product.doAfterTextChanged {
            if (holder.product.text.isEmpty()) {
                holder.product.error = "Vælg produkt"
            } else {
                holder.product.error = null
            }
        }

        holder.product.setText(purchase.storeItem.product.name, false)

        holder.product.setOnItemClickListener { parent, _, pos, _ ->
            val product = products.find { it.name  == parent.getItemAtPosition(pos) }!!

            holder.product.setText(product.name, false)
            viewModel.onProductChanged(holder.adapterPosition, product, currentList)
            insertAllDefaults(holder, viewModel.getPurchase(holder.adapterPosition, currentList))
        }
    }

    private fun insertAllDefaults(holder: ViewHolder, purchase: Purchase) {
        insertCountryDefault(holder, purchase)
        insertAmountDefault(holder, purchase)
        insertWeightDefault(holder, purchase)
    }


    private fun insertCountryDefault(holder: ViewHolder, purchase: Purchase) {


        val countryName = purchase.storeItem.country.name

        if ((countryName == "" && purchase.storeItem.product.name != "")) {
            val countryId = purchase.storeItem.product.countryId
            val country = countries.find { it.id == countryId }!!

                holder.country.setText(country.name,false)
                holder.country.setTextColor( defaultTextColor)
                viewModel.onCountryChanged(holder.adapterPosition,country,currentList)
                viewModel.onCountryDefaultChanged(holder.adapterPosition,true,currentList)
                purchase.storeItem.countryDefault = true
            }else if(purchase.storeItem.countryDefault) {
            val countryId = purchase.storeItem.product.countryId
            val country = countries.find { it.id == countryId }!!
                holder.country.setText(country.name,false)
                purchase.storeItem.countryDefault = true
                holder.country.setTextColor( defaultTextColor)
            }else {
                    holder.country.setText(purchase.storeItem.country.name, false)
                }

    }

    private fun insertWeightDefault(holder: ViewHolder,purchase: Purchase){
            val weightString = purchase.storeItem.weightToString(true)
            if((weightString == "" && purchase.storeItem.product.name != "") ){
                val productWeight = purchase.storeItem.product.weight
                holder.weight.setText(productWeight.toInt().toString())
                holder.weight.setTextColor( defaultTextColor)
                viewModel.onWeightChanged(holder.adapterPosition, productWeight/1000,currentList)
                viewModel.onWeightDefaultChanged(holder.adapterPosition,true,currentList)
                purchase.storeItem.weightDefault = true
            }else if (purchase.storeItem.weightDefault){
                holder.weight.setText(purchase.storeItem.product.weight.toInt().toString())
                purchase.storeItem.weightDefault = true
                holder.weight.setTextColor( defaultTextColor)
            }else{
                holder.weight.setText(purchase.storeItem.weightToString(true))
            }
    }

    private fun insertAmountDefault(holder: ViewHolder, purchase: Purchase) {
        val quanity = purchase.quantity
        if ((quanity == 0 && purchase.storeItem.product.name != "") || purchase.quantityDefault) {
            holder.amount.setText("1")
            holder.amount.setTextColor(defaultTextColor)
            viewModel.onQuantityChanged(holder.adapterPosition, 1, currentList)
            viewModel.onQuantityDefaultChanged(holder.adapterPosition, true, currentList)
            purchase.quantityDefault = true

        } else {
            holder.amount.setText(purchase.quantityToString())
        }
    }

    private fun setUpCountryDropdown(holder: ViewHolder, purchase: Purchase) {
        holder.country.doAfterTextChanged {
            if (holder.country.text.isEmpty()) {
                holder.country.error = "Vælg land"
            } else {
                holder.country.error = null
            }
            holder.country.setTextColor(Color.BLACK)
            purchase.storeItem.countryDefault = false
        }


        insertCountryDefault(holder, purchase)

        holder.country.setOnItemClickListener { parent, view, pos, id ->
            val country = countries.find { it.name == parent.getItemAtPosition(pos)}!!

            holder.country.setText(country.name, false)
            viewModel.onCountryChanged(holder.adapterPosition, country, currentList)
            viewModel.onCountryDefaultChanged(holder.adapterPosition, false, currentList)
        }
    }

    private fun setUpAmountField(holder: ViewHolder, purchase: Purchase) {
        holder.amount.doAfterTextChanged {
            if (holder.amount.text!!.isEmpty()) {
                holder.amount.error = "Indtast antal"
            } else {
                holder.amount.error = null
                viewModel.onQuantityChanged(
                    holder.adapterPosition,
                    it.toString().toInt(),
                    currentList
                )
            }
            holder.amount.setTextColor(Color.BLACK)
            purchase.quantityDefault = false
        }

        insertAmountDefault(holder, purchase)

    }

    private fun setUpWeightField(holder: ViewHolder, purchase: Purchase) {
        holder.weight.doAfterTextChanged {
            if (holder.weight.text!!.isEmpty()) {
                holder.weight.error = "Indtast vægt"
            } else {
                holder.weight.error = null
                viewModel.onWeightChanged(
                    holder.adapterPosition,
                    it.toString().toDouble() / 1000,
                    currentList
                )
                viewModel.onWeightDefaultChanged(holder.adapterPosition, false, currentList)
            }
            holder.weight.setTextColor(Color.BLACK)
            purchase.storeItem.weightDefault = false
        }
        insertWeightDefault(holder, purchase)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toggleButton: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleButton)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete)
        val country: AutoCompleteTextView = itemView.findViewById(R.id.co2Showcase)
        val product: AutoCompleteTextView = itemView.findViewById(R.id.productOption)
        val weight: TextInputEditText = itemView.findViewById(R.id.weight_input)
        val amount: TextInputEditText = itemView.findViewById(R.id.amount_input)
        val title: TextInputEditText = itemView.findViewById(R.id.card_title)
        val acceptButton: Button = itemView.findViewById(R.id.btn_accept)

        init {
            val productAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item,productsToStrings(products))
            val countryAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, countriesToStrings(countries))
            productAdapter.sort { product, product2 -> product.compareTo(product2) }

            itemView.productOption.setAdapter(productAdapter)
            itemView.productOption.threshold = 0
            itemView.co2Showcase.setAdapter(countryAdapter)
            itemView.co2Showcase.threshold = 0
            this.setIsRecyclable(false)

            toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
                toggleBtnListener()
            }
        }
        private fun productsToStrings(products:List<Product>): List<String> {
            val strings = mutableListOf<String>()
            for(product in products){
                strings.add(product.name)

            }
            return strings.toList()
        }
        private fun countriesToStrings(countries: List<Country>): List<String> {
            val strings = mutableListOf<String>()
            for(country in countries){
                strings.add(country.name)

            }
            return strings.toList()
        }

        private fun toggleBtnListener() {
            // TODO: handle toggle buttom listener
        }
    }

    private fun writePurchaseValues(holder: ViewHolder, purchase: Purchase) {
        System.out.println("ReceiptText: " + purchase.storeItem.receiptText)
        System.out.println("Here :" + holder.absoluteAdapterPosition)
        System.out.println("ProductName: " + purchase.storeItem.product.name)
        System.out.println("CountryName: " + purchase.storeItem.country.name)
        System.out.println("CountryDefault: " + purchase.storeItem.countryDefault)
        System.out.println("TextColor: " + (holder.country.currentTextColor == defaultTextColor))
        System.out.println("Weigth: " + purchase.storeItem.weightToString(true))
        System.out.println("WeigthDefault: " + purchase.storeItem.weightDefault)
        System.out.println("Quantatit: " + purchase.quantity)
        System.out.println("QuantatitDefault: " + purchase.quantityDefault)
        System.out.println("Rating: " + purchase.storeItem.rating)

    }
}
