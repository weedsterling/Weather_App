package com.parlonscode.weatho;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.parlonscode.weatho.models.CurrentWeather;
import com.parlonscode.weatho.utilities.Alert;
import com.parlonscode.weatho.utilities.Api;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String GENERIC_ERROR_MESSAGE = "Oooops une erreur est survenue. Veuillez SVP réessayer.";
	private static final String INTERNET_CONNECTIFY_ERROR_MESSAGE = " Veuillez vérifier que vous etes bel et bien connecté à Internet .";

	private static final Color BLUE_COLOR = Color.decode("#8EA2C6");
	private static final Color WHITE_COLOR = Color.WHITE;
	private static final Font DEFAULT_FONT = new Font("San Francisco", Font.PLAIN, 24);
	private static final Color LIGHT_GRAY_COLOR = (new Color(255, 255, 255, 128));

	private JLabel locationLabel;
	private JLabel timeLabel;
	private JLabel temperatureLabel;
	private JPanel otherInfosPanel;
	private JLabel humidityLabel;
	private JLabel humidityValue;
	private JLabel precipLabel;
	private JLabel precipValue;
	private JLabel summaryLabel;

	private CurrentWeather currentweather;

	public MainFrame(String title) {
		super(title);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		contentPane.setBackground(BLUE_COLOR);

		locationLabel = new JLabel("Abidjan, CI", SwingConstants.CENTER);
		locationLabel.setFont(DEFAULT_FONT);
		locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		locationLabel.setForeground(WHITE_COLOR);
		locationLabel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		timeLabel = new JLabel("...");
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeLabel.setFont(DEFAULT_FONT.deriveFont(18f));
		timeLabel.setForeground(LIGHT_GRAY_COLOR);

		temperatureLabel = new JLabel("--", SwingConstants.CENTER);
		temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureLabel.setForeground(WHITE_COLOR);
		temperatureLabel.setFont(DEFAULT_FONT.deriveFont(160f));

		humidityLabel = new JLabel("Humidité".toUpperCase(), SwingConstants.CENTER);
		humidityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		humidityLabel.setForeground(LIGHT_GRAY_COLOR);
		humidityLabel.setFont(DEFAULT_FONT.deriveFont(12f));

		humidityValue = new JLabel("--".toUpperCase(), SwingConstants.CENTER);
		humidityValue.setAlignmentX(Component.CENTER_ALIGNMENT);
		humidityValue.setForeground(WHITE_COLOR);
		humidityValue.setFont(DEFAULT_FONT);

		precipLabel = new JLabel("Risque de Pluie".toUpperCase(), SwingConstants.CENTER);
		precipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		precipLabel.setForeground(LIGHT_GRAY_COLOR);
		precipLabel.setFont(DEFAULT_FONT.deriveFont(12f));

		precipValue = new JLabel("--".toUpperCase(), SwingConstants.CENTER);
		precipValue.setAlignmentX(Component.CENTER_ALIGNMENT);
		precipValue.setForeground(WHITE_COLOR);
		precipValue.setFont(DEFAULT_FONT);

		otherInfosPanel = new JPanel(new GridLayout(2, 2));
		otherInfosPanel.setBackground(BLUE_COLOR);

		otherInfosPanel.add(humidityLabel);
		otherInfosPanel.add(precipLabel);
		otherInfosPanel.add(humidityValue);
		otherInfosPanel.add(precipValue);

		summaryLabel = new JLabel("Récupération de la température actuelle...", SwingConstants.CENTER);
		summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		summaryLabel.setForeground(WHITE_COLOR);
		summaryLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
		summaryLabel.setFont(DEFAULT_FONT.deriveFont(14f));

		contentPane.add(locationLabel);
		contentPane.add(timeLabel);
		contentPane.add(temperatureLabel);
		contentPane.add(otherInfosPanel);
		contentPane.add(summaryLabel);

		setContentPane(contentPane);

		double latitude = 5.345317;
		double longitude = -4.024429;

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(Api.getforecastUrl(latitude, longitude)).build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) {
				try (ResponseBody body = response.body()) {
					if (response.isSuccessful()) {
						String jsondata = body.string();
						currentweather = getCurrentWeatherDetails(jsondata);
						
						EventQueue.invokeLater(() -> updateScreen());

					} else {
						Alert.error(MainFrame.this, GENERIC_ERROR_MESSAGE);

					}
				} catch (ParseException | IOException e) {
					Alert.error(MainFrame.this, GENERIC_ERROR_MESSAGE);
				}

			}

			@Override
			public void onFailure(Call call, IOException e) {

				Alert.error(MainFrame.this, INTERNET_CONNECTIFY_ERROR_MESSAGE);

			}

		});

	}

	protected void updateScreen() {
		timeLabel.setText("Il est " + currentweather.getFormattedTime() + " et la température actuel est de :");
		temperatureLabel.setText(currentweather.getTemperature() + "°");
		humidityValue.setText(currentweather.getHumidity() + "");
		precipValue.setText(currentweather.getPrecipProbability() + "%");
		summaryLabel.setText(currentweather.getSummary());
	}

	private CurrentWeather getCurrentWeatherDetails(String jsondata) throws ParseException {
		CurrentWeather currentweather = new CurrentWeather();
		JSONObject forecast = (JSONObject) JSONValue.parseWithException(jsondata);
		JSONObject currently = (JSONObject) forecast.get("currently");

		currentweather.setTimezone((String) forecast.get("timezone"));
		currentweather.setTime((long) currently.get("time"));
		currentweather.setTemperature(Double.parseDouble(currently.get("temperature") + ""));
		currentweather.setPrecipProbability(Double.parseDouble(currently.get("precipProbability") + ""));
		currentweather.setSummary((String) currently.get("summary"));
		currentweather.setHumidity(Double.parseDouble(currently.get("humidity") + ""));

		return currentweather;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);

	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();

	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();

	}
}
