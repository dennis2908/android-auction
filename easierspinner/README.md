# EasierSpinner
A library made to simplify Spinner objects.

This library was created because I feel that creating a Spinner object in Android takes too much effort and lines. Especially when I need a Spinner that has a "hint" like an EditText.

# Features
* Create a Spinner with a placeholder item (similar to an EditText hint).
* Simplify managing the List of data to be used by the Spinner.
* Customizable "Refresh" image and image color (using `PorterDuff.Mode.SRC_IN`).
* Customizable adapter for the view, placeholder view, and dropdown view.

# Usage
* To use a simple Spinner that contains a List of String, simply use the `StringSpinner` class in your XML:

        List<String> data = new ArrayList<>();
        data.add("Bicycle");
        data.add("Motorcycle");
        data.add("Car");
        stringSpinner.setData(data);
        stringSpinner.setPlaceholder("Pick a vehicle");
        stringSpinner.showPlaceholder();

* To use a customized Spinner that contains a List of your `CustomData`, create a class that extends from `EasierSpinnerCore` and specify your `CustomData`. For example:

        public class VehicleSpinner extends EasierSpinnerCore<Vehicle> {
            @Override
            public void onPrepare() {    
                List<Vehicle> data = new ArrayList<>();
                data.add(new Vehicle("Bicycle"));
                data.add(new Vehicle("Motorcycle"));
                data.add(new Vehicle("Car"));
                setData(data);
                setPlaceholderText("Pick a vehicle");
                showPlaceholder();
            }
        }

* In this case, the `Vehicle` class will need to implement `SpinnerText`, and override the `getSpinnerText()` to determine what to show in the Spinner. In this example:

        public class Vehicle implements EasierSpinnerStringAdapter.SpinnerText {
            private String vehicleName;

            @Override
            public String getSpinnerText() {
                return vehicleName;
            }
        }

* If the Spinner content is obtained remotely (via API, or such), you can create a Spinner that extends from  `RefreshableEasierSpinnerCore` instead. The Spinner will have a Refresh button on the right (or end). Simply call `showRefreshIcon()` or `dismissRefreshIcon()` and define what to do if its clicked in `onSpinnerRefresh()`.

# Using custom adapter
* To use a custom adapter, create an adapter class that extends from `EasierSpinnerAdapterCore` and define the adapter there. The adapter behaves quite similarly with `RecyclerView.Adapter`, except this Spinner uses 3 `ViewHolder` classes (for the placeholder, adapter, and dropdown respectively). For example:

        public class CustomAdapter<CustomData> extends EasierSpinnerAdapterCore<CustomData, CustomAdapter.PlaceholderViewHolder, CustomAdapter.AdapterViewHolder, CustomAdapter.DropdownViewHolder> {

            public CustomAdapter(Context context) {
                super(context);
            }

            @Override
            public CustomAdapter.PlaceholderViewHolder onCreatePlaceholderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_spinner_placeholder_item, parent, false);
                return new PlaceholderViewHolder(view);
            }

            @Override
            public CustomAdapter.AdapterViewHolder onCreateSelectionViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_spinner_item, parent, false);
                return new AdapterViewHolder(view);
            }

            @Override
            public CustomAdapter.DropdownViewHolder onCreateDropdownViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_spinner_dropdown_item, parent, false);
                return new DropdownViewHolder(view);
            }

            @Override
            public void onBindPlaceholderViewHolder(CustomAdapter.PlaceholderViewHolder holder, CustomData placeholder) {
                holder.mTextView.setText(placeholder.toString());
            }

            @Override
            public void onBindSelectionViewHolder(CustomAdapter.AdapterViewHolder holder, int position, CustomData data) {
                holder.mTextView.setText(data.toString());
                holder.mImageView.setImageResource(data.getImageRes());
            }

            @Override
            public void onBindDropdownViewHolder(CustomAdapter.DropdownViewHolder holder, int position, CustomData data, boolean isPlaceholder) {
                holder.mTextView.setText(data.toString());
                holder.mImageView.setImageResource(data.getImageRes());
            }

            public class PlaceholderViewHolder extends EasierSpinnerAdapterViewHolder {

                TextView mTextView;

                public PlaceholderViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView.findViewById(R.id.text);
                }
            }
            
            public class AdapterViewHolder extends EasierSpinnerAdapterViewHolder {

                TextView mTextView;
                ImageView mImageView;

                public AdapterViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView.findViewById(R.id.text);
                    mImageView = (ImageView) convertView.findViewById(R.id.image);
                }
            }

            public class DropdownViewHolder extends EasierSpinnerAdapterViewHolder {

                TextView mTextView;
                ImageView mImageView;

                public DropdownViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView.findViewById(R.id.text);
                    mImageView = (ImageView) convertView.findViewById(R.id.image);
                }
            }
        }

* For simpler adapter, you can extend from `EasierSpinnerAdapterSimpleCore` to only specify one `ViewHolder` for all three different states. Keep in mind that when binding the ViewHolder, the data may be your placeholder data. For example:

        public class CustomAdapter<CustomData> extends EasierSpinnerAdapterSimpleCore<CustomData, CustomAdapter.ViewHolder> {

            public CustomAdapter(Context context) {
                super(context);
            }

            @Override
            public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_spinner_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position, CustomData data, boolean isPlaceholder) {
                holder.mTextView.setText(data.toString());
                holder.mImageView.setImageResource(data.getImageRes());
            }
            
            public class ViewHolder extends EasierSpinnerAdapterViewHolder {

                TextView mTextView;
                ImageView mImageView;

                public ViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView.findViewById(R.id.text);
                    mImageView = (ImageView) convertView.findViewById(R.id.image);
                }
            }
        }

* To use your custom adapter on your custom Spinner, simply set your adapter with `setAdapter()` and pass your custom adapter. For example:
  * For example 

            public class VehicleSpinner extends EasierSpinnerCore<Vehicle> {
                @Override
                public void onPrepare() {
                    setAdapter(new CustomAdapter(getContext()));
                    
                    // Think of this as an Activity's onCreate, do stuff here
                }
            }

  * Or

            VehicleSpinner mySpinner = findViewById(R.id.my_spinner);
            mySpinner.setAdapter(new CustomAdapter(getContext()));

  * <strong>Remember that setting the adapter with a new one will clear any set data and placeholder!</strong>

# Customization
`RefreshableEasierSpinnerCore` has several attributes that can be modified:

### easyspinner_refreshImage
Determines the refresh image drawable.

### easyspinner_refreshImageColor
Determines the refresh image color. Will use `PorterDuff.Mode.SRC_IN` when set via XML.

# Notes
* The data for this library's Spinners can <strong>only</strong> be specified in java.