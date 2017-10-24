package group_8.project_evnt;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import group_8.project_evnt.core.Database;
import group_8.project_evnt.models.PollAnswer;
import group_8.project_evnt.utils.AppUtils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPollFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_ROOM_ID = "roomid";
    private static final String ARG_POLL_ID = "pollid";

    private String currentRoomId;
    private ArrayList<PollAnswer> pollAnswers = new ArrayList<>();

    private RecyclerView mPollAlternativeRecycleView;
    private ImageButton menuButton;
    private Button publishButton;
    private EditText etTitle, etQuestion;
    private ImageButton closeButton;



    private LinearLayoutManager mLinearLayoutManager;
    private AddAlternativeAdapter mAddAlternativeAdapter;

    public AddPollFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddPollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPollFragment newInstance(String pollId) {
        AddPollFragment fragment = new AddPollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POLL_ID, pollId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentRoomId = getArguments().getString(ARG_ROOM_ID);
        } else {
            return;
        }


        pollAnswers.add(new PollAnswer());
        //mAddAlternativeAdapter.notifyDataSetChanged();

//        DatabaseReference poll = Database.getInstance().poll(currentPollId);
//        poll.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                pollAnswers.clear();
//                for (DataSnapshot alternative : dataSnapshot.getChildren()){
//                    Log.d("Data", alternative.toString());
//                    pollAnswers.add(alternative.getValue(PollAlternative.class));
//                }
//                if(mAddAlternativeAdapter != null) {
//                    mAddAlternativeAdapter.notifyDataSetChanged();
//                }
//                if(mLinearLayoutManager != null) {
//                    mLinearLayoutManager.scrollToPosition(pollAnswers.size() - 1);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    /** The system calls this only when creating the layout in a dialog. */
//    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_poll, container, false);

        mPollAlternativeRecycleView = view.findViewById(R.id.rv_alternatives);

        menuButton = view.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(this);

        publishButton = view.findViewById(R.id.publish_button);
        publishButton.setOnClickListener(this);

        menuButton = view.findViewById(R.id.button_add_poll_dismiss);
        menuButton.setOnClickListener(this);


        etTitle = view.findViewById(R.id.text_input_title);
        etQuestion = view.findViewById(R.id.text_input_question);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup adapter
        // Create adapter passing in the sample user data
        mAddAlternativeAdapter = new AddAlternativeAdapter(this.getActivity(), pollAnswers);
        // Attach the adapter to the recyclerview to populate items
        mPollAlternativeRecycleView.setAdapter(mAddAlternativeAdapter);
        // Set layout manager to position the items
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mPollAlternativeRecycleView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.menu_button:
                showPopup(v);
                break;

            case R.id.publish_button:
                publishPoll(v);
                break;

            case R.id.button_add_poll_dismiss:
                getActivity().onBackPressed();
                break;
        }
        
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_poll_card, popup.getMenu());
        popup.show();
    }

    private void publishPoll(View v) {

        Toast toast;
        if (etTitle.getText().toString().equals("")) {
            toast = Toast.makeText(getContext(), "Enter a title", Toast.LENGTH_SHORT);
        }
        else if (etQuestion.getText().toString().equals("")) {
            toast = Toast.makeText(getContext(), "Enter a question", Toast.LENGTH_SHORT);
        } else if (pollAnswers.size() < 3) {
            toast = Toast.makeText(getContext(), "Enter at least two alternatives", Toast.LENGTH_SHORT);
        } else {
            // if successful
            toast = Toast.makeText(getContext(), "The poll has been published :)", Toast.LENGTH_SHORT);
            // change the button to withdraw
            publishButton.setText("WITHDRAW");
            publishButton.setBackgroundColor(Color.RED);

            // save the poll and send to server
            final Database db = Database.getInstance();

            final String title = etTitle.getText().toString();
            final String question = etQuestion.getText().toString();
            final String userId = AppUtils.getDeviceId(getContext());
            db.createPoll(currentRoomId, title, question, userId, pollAnswers, true);
        }

        if (toast != null)
            toast.show();

        getActivity().onBackPressed();


    }

    // Create the basic adapter extending from RecyclerView.Adapter
    // Note that we specify the custom ViewHolder which gives us access to our views
    public class AddAlternativeAdapter extends
            RecyclerView.Adapter<AddAlternativeAdapter.ViewHolder> {

        private ArrayList<PollAnswer> mPollAnswers;
        private Context mContext;

        // Pass in the contact array into the constructor
        public AddAlternativeAdapter(Context context, ArrayList<PollAnswer> pollAlternatives) {
            mPollAnswers = pollAlternatives;
            mContext = context;
        }

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView text_plus_sign;
            public EditText et_alternative;
            private Context context;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(Context context, View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                // store context
                this.context = context;

                text_plus_sign = (TextView) itemView.findViewById(R.id.plus_sign);
                et_alternative = (EditText) itemView.findViewById(R.id.add_alternative);
                et_alternative.addTextChangedListener(this);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int position = -1;
                if (et_alternative.getTag() != null)
                    position = (int) et_alternative.getTag();

                if (position < 0){
                    return;
                }

                String answer = et_alternative.getText().toString();
                pollAnswers.get(position).setAnswer(answer);

                if (position == mPollAnswers.size()-1 && !et_alternative.getText().toString().equals("")){
                    pollAnswers.add(new PollAnswer());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }

        @Override
        public AddPollFragment.AddAlternativeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View yourMessageView = inflater.inflate(R.layout.fragment_add_alt, parent, false);

            // Return a new holder instance
            AddPollFragment.AddAlternativeAdapter.ViewHolder viewHolder = new AddPollFragment.AddAlternativeAdapter.ViewHolder(context, yourMessageView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(AddPollFragment.AddAlternativeAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            PollAnswer alt = mPollAnswers.get(position);

            // Set item views based on your views and data model
            EditText alternative = viewHolder.et_alternative;
            alternative.setText(alt.getAnswer());
            alternative.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mPollAnswers.size();
        }
    }
}
