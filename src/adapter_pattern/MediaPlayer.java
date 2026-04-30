package adapter_pattern;

interface MediaPlayer {
    void play(String filename);
}

class Mp3Player implements MediaPlayer {
    @Override
    public void play(String filename) {
        System.out.println("Playing MP3 file: " + filename);
    }
}

class VLCPlayer {
    public void playVLC(String filename) {
        System.out.println("Playing VLC file: " + filename);
    }
}

class MediaPlayerAdapter implements MediaPlayer {
    private final VLCPlayer vlcPlayer;

    public MediaPlayerAdapter(VLCPlayer vlcPlayer) {
        this.vlcPlayer = vlcPlayer;
    }

    @Override
    public void play(String filename) {
        if (filename.endsWith(".vlc")) {
            vlcPlayer.playVLC(filename);
        } else {
            System.out.println("Unsupported format: " + filename);
        }
    }
}



class MediaPlayerApp {
    public static void main(String[] args) {
        MediaPlayer mp3Player = new Mp3Player();
        mp3Player.play("song.mp3");

        VLCPlayer vlcPlayer = new VLCPlayer();
        MediaPlayer mediaAdapter = new MediaPlayerAdapter(vlcPlayer);
        mediaAdapter.play("movie.vlc");
        
    }

}
