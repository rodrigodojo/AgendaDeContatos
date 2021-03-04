package com.dojo.agendadecontatos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.InputStream;

public class Formulario extends AppCompatActivity {

    FormularioHelper helper ;
    Contato contato ;
    private String localArquivoFoto;
    private static final int TIRA_FOTO = 123;
    private boolean fotoResource = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new FormularioHelper(this);

        if(toolbar != null){
            toolbar.setTitle("Editar Contato");
            toolbar.setNavigationIcon(R.drawable.ic_voltar_foreground);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(Formulario.this);
            }
        });

        Intent intent = this.getIntent();
        this.contato = (Contato) intent.getSerializableExtra("contatoSelecionado");
        if(this.contato != null){
            this.helper.colocaNoFormulario(contato);
        }

        final ImageButton botaoFoto = helper.getBotaoFoto();
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaSourceImagem();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                Contato contato = helper.pegaContatoDoFormulario();
                ContatoDAO dao = new ContatoDAO(Formulario.this);

                if(contato.getId() == null){
                    dao.inserirContato(contato);
                }else{
                    dao.alterarContato(contato);
                }
                dao.close();
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void carregaFotoCamera(){
        fotoResource = true;
        localArquivoFoto = getExternalFilesDir(null)+"/"+System.currentTimeMillis()+".jpg";
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(localArquivoFoto)));
        startActivityForResult(intentCamera,123);
    }


    public void carregaFotoBiblioteca(){
        fotoResource = false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem:"),1);
    }

    public void alertaSourceImagem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Selecione a fonte da imagem:");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carregaFotoCamera();
            }
        });

        builder.setNegativeButton("Biblioteca", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carregaFotoBiblioteca();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!fotoResource) {

        }else{
                if (requestCode == TIRA_FOTO) {
                    if (resultCode == Activity.RESULT_OK) {
                        helper.carregaImagem(this.localArquivoFoto);
                    } else {
                        this.localArquivoFoto = null;
                    }


            }
        }
    }
}