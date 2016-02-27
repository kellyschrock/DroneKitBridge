package com.fognl.dronekitbridge.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fognl.dronekitbridge.DKBridgeApp;
import com.fognl.dronekitbridge.R;
import com.fognl.dronekitbridge.comm.SocketServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ServerFragment extends Fragment {
    static final String TAG = ServerFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_listen: {
                    onListenClick(v);
                    break;
                }
            }
        }
    };

    private final TextWatcher mWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            setButtonStates();
        }
    };

    private final SocketServer.Listener mServerListener = new SocketServer.Listener() {
        @Override
        public void onStarted() {
            mButton.setText(R.string.btn_stop_listening);
        }

        @Override
        public void onStopped() {
            mButton.setText(R.string.btn_listen);
        }

        @Override
        public void onClientDisconnected() {
            Toast.makeText(getActivity(), R.string.toast_client_disconnected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClientConnected() {
            Toast.makeText(getActivity(), R.string.toast_client_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onData(String data) {
            Log.v(TAG, "onData(): data=" + data);

            if(mLogIncomingData) {
                mLogText.append(data + "\n");
            }
        }

        @Override
        public void onError(Throwable error) {
            showError(error);
            Log.e(TAG, error.getMessage(), error);
        }

        @Override
        public void onLocalIpFound(String ip) {
            Log.v(TAG, "onLocalIpFound(): ip=" + ip);
        }
    };

    private TextView mIpAddrText;
    private TextView mLogText;
    private EditText mPortEditText;
    private Button mButton;

    private SocketServer mServer;
    private Thread mServerThread;
    private boolean mLogIncomingData;

    public ServerFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIpAddrText = (TextView)view.findViewById(R.id.text_ip_addr);
        mLogText = (TextView)view.findViewById(R.id.text_log);
        mButton = (Button)view.findViewById(R.id.btn_listen);
        mPortEditText = (EditText)view.findViewById(R.id.edit_port);
        mPortEditText.addTextChangedListener(mWatcher);

        mButton.setOnClickListener(mClickListener);

        mPortEditText.setText(String.valueOf(SocketServer.DEFAULT_PORT));

        ((CheckBox)view.findViewById(R.id.chk_log)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLogIncomingData = isChecked;
            }
        });

        try {
            String ip = SocketServer.getLocalIpAddress();
            mIpAddrText.setText(ip);
        } catch(Throwable ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // When switching to this fragment
        Log.v(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        // When switching to another app
        Log.v(TAG, "onStop()");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Getting onto the screen
        Log.v(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Leaving the screen
        Log.v(TAG, "onPause()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Before onDestroy()
        Log.v(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Just before actually dying
        Log.v(TAG, "onDestroy()");

        if(mServer != null) {
            try {
                stopServer();
            }
            catch(Throwable ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void onListenClick(View v) {
        if(mServer != null) {
            // Already running, stop
            stopServer();
        } else {
            // Not running, start.
            startServer();
        }
    }

    void startServer() {
        mLogText.setText("");
        mServer = new SocketServer(DKBridgeApp.get().getHandler(), mServerListener);
        mServerThread = new Thread(mServer);
        mServerThread.start();
    }

    void stopServer() {
        mServer.cancel();

        try {
            mServerThread.join(5000);
        }
        catch(InterruptedException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        mServer = null;
        mServerThread = null;
    }

    void setButtonStates() {
        boolean enabled = true;

        if(TextUtils.isEmpty(mPortEditText.getText().toString())) {
            enabled = false;
        }

        getView().findViewById(R.id.btn_listen).setEnabled(enabled);
    }

    void showError(Throwable error) {
        final Activity activity = getActivity();
        if(activity != null && !activity.isDestroyed()) {
            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
